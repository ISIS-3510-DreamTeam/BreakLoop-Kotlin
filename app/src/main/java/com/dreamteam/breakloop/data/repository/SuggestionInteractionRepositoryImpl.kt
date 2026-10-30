package com.dreamteam.breakloop.data.repository

import com.dreamteam.breakloop.data.local.dao.SuggestionInteractionDao
import com.dreamteam.breakloop.data.local.entity.SuggestionInteractionEntity
import com.dreamteam.breakloop.data.remote.SuggestionInteractionRemoteDataSource
import com.dreamteam.breakloop.data.remote.dto.ShownSuggestionDto
import com.dreamteam.breakloop.data.remote.mapper.toDto
import com.dreamteam.breakloop.domain.ContextSnapshot
import com.dreamteam.breakloop.domain.enums.ActivityCategory
import com.dreamteam.breakloop.domain.enums.InteractionOutcome
import com.dreamteam.breakloop.domain.enums.PlaceLabel
import com.dreamteam.breakloop.domain.enums.ReasonTag
import com.dreamteam.breakloop.domain.repository.SuggestionInteractionRepository
import com.dreamteam.breakloop.domain.sync.SyncScheduler
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.ZoneId
import java.util.UUID

class SuggestionInteractionRepositoryImpl(
    private val dao: SuggestionInteractionDao,
    private val remote : SuggestionInteractionRemoteDataSource,
    private val syncScheduler: SyncScheduler
): SuggestionInteractionRepository {
    override suspend fun openInteraction(
        context: ContextSnapshot,
        now: Long,
        placeLabel: PlaceLabel?
    ): String {
        val id = UUID.randomUUID().toString()
        val day = Instant.ofEpochMilli(now).atZone(ZoneId.systemDefault()).dayOfWeek.value
        dao.upsert(
            entity = SuggestionInteractionEntity(
                id = id,
                openedAt = now,
                closedAt = null,
                timeOfDay = context.timeOfDay,
                isWeekend = day == 6 || day == 7,
                weather = context.weather?.name,
                placeLabel = placeLabel,
                latitude = context.latitude,
                longitude = context.longitude,
                shownSuggestionsJson = "[]",
                outcome = InteractionOutcome.PENDING,
                acceptedActivityId = null,
                chosenActivityId = null,
                updatedAt = now,
                synced = false
            )
        )

        syncScheduler.enqueueSuggestionInteractionSync()
        return id
    }

    override suspend fun addShownSuggestion(
        id: String,
        activityId: String,
        category: ActivityCategory,
        availableMin: Int,
        score: Double,
        reasons: List<ReasonTag>
    ) {
        val actual = dao.getById(id) ?: return
        if ( actual.outcome != InteractionOutcome.PENDING) return
        val list = Json.decodeFromString<List<ShownSuggestionDto>>(actual.shownSuggestionsJson)
        val last = list.lastOrNull()
        if ( last != null && last.activityId == activityId && last.availableMin == availableMin) return
        val now = System.currentTimeMillis()
        val new = ShownSuggestionDto(
            activityId,
            category.name,
            availableMin,
            score,
            reasons.map { it.name },
            shownAt = now
        )

        dao.upsert(actual.copy(
            shownSuggestionsJson = Json.encodeToString(list + new),
            updatedAt = now,
            synced = false
        ))
        syncScheduler.enqueueSuggestionInteractionSync()
    }

    override suspend fun closeInteraction(
        id: String,
        outcome: InteractionOutcome,
        acceptedActivityId: String?,
        chosenActivityId: String?
    ) {
        val actual = dao.getById(id) ?: return
        if ( actual.outcome != InteractionOutcome.PENDING) return
        val now = System.currentTimeMillis()

        dao.upsert(actual.copy(
            outcome = outcome,
            acceptedActivityId = acceptedActivityId,
            chosenActivityId = chosenActivityId,
            closedAt = now,
            updatedAt = now,
            synced = false
            )
        )

        syncScheduler.enqueueSuggestionInteractionSync()

    }

    override suspend fun updatePlace(
        id: String,
        placeLabel: PlaceLabel?
    ) {
        val actual = dao.getById(id) ?: return
        if ( actual.outcome != InteractionOutcome.PENDING) return
        dao.upsert(
            actual.copy(
                placeLabel = placeLabel, updatedAt = System.currentTimeMillis(),
                synced = false
            )
        )
        syncScheduler.enqueueSuggestionInteractionSync()
    }

    override suspend fun syncUnsynced() {
        val pending = dao.getUnsynced(500)
        if (pending.isEmpty()) return
        remote.upload(pending.map { it.toDto() })
        for ( p in pending) {
            dao.markSynced(p.id, p.updatedAt)
        }
    }

}