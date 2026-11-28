package com.dreamteam.breakloop.data.achievement

import android.content.Context
import android.util.Log
import com.dreamteam.breakloop.data.goal.DataStoreGoalRepository
import com.dreamteam.breakloop.data.local.AppDatabase
import com.dreamteam.breakloop.data.local.entity.UserAchievementEntity
import com.dreamteam.breakloop.data.local.mapper.toDomain
import com.dreamteam.breakloop.data.remote.RetrofitInstance
import com.dreamteam.breakloop.domain.Achievement
import com.dreamteam.breakloop.domain.AchievementCatalog
import com.dreamteam.breakloop.domain.AchievementInput
import com.dreamteam.breakloop.domain.AchievementProgress
import com.dreamteam.breakloop.domain.AchievementRule
import com.dreamteam.breakloop.domain.CatalogSource
import com.dreamteam.breakloop.domain.DayUsage
import com.dreamteam.breakloop.domain.repository.GoalRepository
import com.dreamteam.breakloop.domain.usecase.CalculateStreakUseCase
import com.dreamteam.breakloop.domain.usecase.EvaluateAchievementsUseCase
import com.dreamteam.breakloop.remote.AchievementApi
import com.dreamteam.breakloop.remote.AchievementDto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import com.dreamteam.breakloop.data.remote.UsageRemoteDataSource
import com.dreamteam.breakloop.data.repository.FlaggedAppsRepositoryImpl
import com.dreamteam.breakloop.data.repository.UsageRepositoryImpl
import com.dreamteam.breakloop.data.sync.WorkManagerSyncScheduler
import com.dreamteam.breakloop.data.system.InstalledAppsDataSource
import com.dreamteam.breakloop.data.system.PermissionsDataSource
import com.dreamteam.breakloop.data.system.UsageEventsDataSource
import com.dreamteam.breakloop.domain.repository.UsageRepository
import com.dreamteam.breakloop.domain.usecase.AggregateUsageEventsUseCase
import com.dreamteam.breakloop.domain.usecase.CalculateBaselineUseCase
import com.dreamteam.breakloop.domain.usecase.CalculateFlaggedTimeUseCase
import com.google.firebase.auth.FirebaseAuth
import com.dreamteam.breakloop.remote.AchievementUnlockRequest

class AchievementRepository(
    private val context: Context,
    private val api: AchievementApi = RetrofitInstance.achievementApi,
    private val db: AppDatabase = AppDatabase.getInstance(context),
    private val usageRepository: UsageRepository = buildUsageRepository(context, db),
    private val goalRepository: GoalRepository = DataStoreGoalRepository(context),
    private val catalogStorage: AchievementCatalogStorage = AchievementCatalogStorage(context),
    private val preferences: AchievementPreferences = AchievementPreferences(context),
    private val evaluateAchievements: EvaluateAchievementsUseCase = EvaluateAchievementsUseCase(),
    private val calculateStreak: CalculateStreakUseCase = CalculateStreakUseCase(),
) {

    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Network first. Without network: the last catalog saved in the local file,
     * and on a first launch without network, the copy bundled in assets.
     */
    suspend fun loadCatalog(): AchievementCatalog = withContext(Dispatchers.IO) {
        try {
            val dtos = api.getCatalog()
            catalogStorage.save(json.encodeToString(dtos))
            AchievementCatalog(dtos.toDomain(), CatalogSource.NETWORK)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val saved = catalogStorage.read()
            if (saved != null) {
                AchievementCatalog(parse(saved), CatalogSource.SAVED)
            } else {
                AchievementCatalog(parse(catalogStorage.readBundled()), CatalogSource.BUNDLED)
            }
        }
    }

    /**
     * Rule engine: a parent coroutine on IO launches one child per metric in parallel,
     * the rules run on Default, and the unlocks are stored in Room.
     */
    suspend fun evaluate(uid: String, catalog: List<Achievement>): List<AchievementProgress> {
        val startNs = System.nanoTime() // measured for the micro-optimization (Logcat tag AchievementsPerf)
        val today = LocalDate.now()

        val input = withContext(Dispatchers.IO) {
            coroutineScope {
                val days = async {
                    // The days must be in Room before reading them; otherwise the first evaluation
                    // at app start sees no history (streak 0). Same 7 days as StreakViewModel.
                    usageRepository.backfillMissingDays(today.minusDays(BACKFILL_DAYS), today.minusDays(1))
                    loadDays(today)
                }
                val goalMs = async { goalRepository.observeDailyGoalMs().first() }
                val focus = async { db.focusSessionDao().countCompleted(uid) }
                val streak = async { calculateStreak.calculate(days.await(), goalMs.await(), today) }

                AchievementInput(
                    days = days.await()
                        .filter { !it.isPartial && LocalDate.parse(it.date).isBefore(today) }
                        .map { DayUsage(LocalDate.parse(it.date), (it.screenTimeMs / 60_000).toInt()) },
                    dailyLimitMinutes = goalMs.await()?.let { (it / 60_000).toInt() },
                    streak = streak.await(),
                    completedFocusSessions = focus.await(),
                )
            }
        }

        // CPU work: the rules run on the pool meant for calculations
        val evaluated = withContext(Dispatchers.Default) {
            evaluateAchievements.evaluate(catalog, input)
        }

        return withContext(Dispatchers.IO) {
            val dao = db.userAchievementDao()
            val now = System.currentTimeMillis()

            // Once unlocked, an achievement stays unlocked even if the streak breaks later
            dao.insertAll(
                evaluated.filter { it.unlocked }
                    .map { UserAchievementEntity(uid = uid, achievementId = it.achievement.id, unlockedAt = now) }
            )
            val stored = dao.getForUser(uid).associateBy { it.achievementId }

            val result = evaluated.map { progress ->
                val row = stored[progress.achievement.id]
                if (row == null) progress
                else progress.copy(unlocked = true, unlockedAt = row.unlockedAt, synced = row.synced)
            }
            result.forEach { AchievementProgressCache.put(uid, it) }
            // Eventual connectivity: new unlocks are uploaded later by the worker, only with network
            if (stored.values.any { !it.synced }) AchievementSyncWorker.schedule(context)
            Log.d("AchievementsPerf", "evaluate took ${(System.nanoTime() - startNs) / 1_000_000.0} ms")
            result
        }
    }

    /** Only called by AchievementSyncWorker. Throws when the upload must be retried. */
    suspend fun syncUnlocks() {
        val dao = db.userAchievementDao()
        for (row in dao.getUnsynced()) {
            val response = api.unlock(row.uid, row.achievementId, AchievementUnlockRequest(row.unlockedAt))
            when {
                response.isSuccessful -> dao.markSynced(row.uid, row.achievementId)
                // The backend no longer knows this achievement: retrying would never work
                response.code() == 404 -> dao.markSynced(row.uid, row.achievementId)
                else -> throw IllegalStateException("Achievement sync failed: HTTP ${response.code()}")
            }
        }
    }

    /** LruCache first; on a miss (for example, the app was killed) the rules are evaluated again. */
    suspend fun getProgress(uid: String, achievementId: String): AchievementProgress? {
        AchievementProgressCache.get(uid, achievementId)?.let { return it }
        val catalog = loadCatalog()
        return evaluate(uid, catalog.achievements).firstOrNull { it.achievement.id == achievementId }
    }

    /** Unlocked achievements whose celebration the user has not seen yet. */
    suspend fun pendingCelebrations(uid: String, progress: List<AchievementProgress>): List<AchievementProgress> {
        val seen = preferences.getSeenCelebrations(uid)
        return progress.filter { it.unlocked && it.achievement.id !in seen }
    }

    suspend fun markCelebrationSeen(uid: String, achievementId: String) {
        preferences.markCelebrationSeen(uid, achievementId)
    }

    private suspend fun loadDays(today: LocalDate) =
        db.dailyUsageStatsDao()
            .getDailyUsageStatsByRangeOnce(today.minusDays(HISTORY_DAYS).toString(), today.toString())
            .map { it.toDomain() }

    private fun parse(text: String): List<Achievement> =
        json.decodeFromString<List<AchievementDto>>(text).toDomain()

    /** An unknown rule (added later in the backend) is skipped instead of breaking the app. */
    private fun List<AchievementDto>.toDomain(): List<Achievement> = mapNotNull { dto ->
        val rule = AchievementRule.entries.firstOrNull { it.name == dto.rule } ?: return@mapNotNull null
        Achievement(dto.id, dto.name, dto.description, dto.image, rule, dto.target)
    }

    companion object {
        private const val HISTORY_DAYS = 60L
        private const val BACKFILL_DAYS = 7L
    }
}

/** Same construction as StreakViewModel and HomeViewModel. */
private fun buildUsageRepository(context: Context, db: AppDatabase): UsageRepository {
    val appContext = context.applicationContext
    return UsageRepositoryImpl(
        usageEventsDataSource = UsageEventsDataSource(
            appContext,
            permissionsDataSource = PermissionsDataSource(appContext)
        ),
        installedAppsDataSource = InstalledAppsDataSource(appContext),
        aggregateUsageEventsUseCase = AggregateUsageEventsUseCase(),
        appUsageDao = db.appUsageDao(),
        dailyUsageStatsDao = db.dailyUsageStatsDao(),
        calculateBaselineUseCase = CalculateBaselineUseCase(),
        calculateFlaggedTimeUseCase = CalculateFlaggedTimeUseCase(),
        flaggedAppsRepositoryImpl = FlaggedAppsRepositoryImpl(db.flaggedAppDao()),
        remote = UsageRemoteDataSource(RetrofitInstance.usageApi, FirebaseAuth.getInstance()),
        syncScheduler = WorkManagerSyncScheduler(appContext)
    )
}