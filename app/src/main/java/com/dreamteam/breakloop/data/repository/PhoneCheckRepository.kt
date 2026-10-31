package com.dreamteam.breakloop.data.local.repository

import android.content.Context
import android.content.SharedPreferences
import com.dreamteam.breakloop.data.local.entity.PhonePickupModel
import com.dreamteam.breakloop.data.remote.PhonePickupRemoteDatasource
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PhoneCheckRepository(
    context: Context,
    private val remoteDatasource: PhonePickupRemoteDatasource =
        PhonePickupRemoteDatasource(),
    private val firebaseAuth: FirebaseAuth =
        FirebaseAuth.getInstance()
) {

    companion object {
        private const val PREFS_NAME = "phone_check_preferences"
        private const val KEY_PICKUPS = "phone_check_timestamps"
        private const val KEY_PICKUP_RECORDS = "phone_pickup_records"
        private const val KEY_MINDFUL_COUNT = "mindful_count"
        private const val KEY_IMPULSIVE_COUNT = "impulsive_count"
    }

    private val preferences: SharedPreferences =
        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    private val syncMutex = Mutex()

    fun savePickup(timestamp: Long) {
        val records = getPickupRecords().toMutableList()

        records.add(
            PhonePickupModel(
                timestamp = timestamp,
                userId = firebaseAuth.currentUser?.uid.orEmpty()
            )
        )

        savePickupRecords(records)

        val timestamps = getPickups().toMutableList()
        timestamps.add(timestamp)

        saveTimestamps(timestamps)
    }

    fun getPickupRecords(): List<PhonePickupModel> {
        val stored = preferences.getString(
            KEY_PICKUP_RECORDS,
            null
        ) ?: return migrateOldPickups()

        return try {
            val array = JSONArray(stored)

            List(array.length()) { index ->
                val item = array.getJSONObject(index)

                PhonePickupModel(
                    pickupId = item.getString("pickupId"),
                    userId = item.optString("userId", ""),
                    timestamp = item.getLong("timestamp"),
                    isSynced = item.optBoolean("isSynced", false),
                    hourOfDay = item.optInt("hourOfDay"),
                    dayOfWeek = item.optInt("dayOfWeek"),
                    isIntentional = item.optBoolean("isIntentional", false)
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun migrateOldPickups(): List<PhonePickupModel> {
        val oldPickups = getPickups()

        val records = oldPickups.map { timestamp ->
            PhonePickupModel(
                timestamp = timestamp,
                userId = firebaseAuth.currentUser?.uid.orEmpty()
            )
        }

        savePickupRecords(records)

        return records
    }

    private fun savePickupRecords(
        records: List<PhonePickupModel>
    ) {
        val array = JSONArray()

        records.forEach { pickup ->
            array.put(
                JSONObject().apply {
                    put("pickupId", pickup.pickupId)
                    put("userId", pickup.userId)
                    put("timestamp", pickup.timestamp)
                    put("isSynced", pickup.isSynced)
                    put("hourOfDay", pickup.hourOfDay)
                    put("dayOfWeek", pickup.dayOfWeek)
                    put("isIntentional", pickup.isIntentional)
                }
            )
        }

        preferences.edit()
            .putString(
                KEY_PICKUP_RECORDS,
                array.toString()
            )
            .apply()
    }

    fun updateLastPickupIntentionality(
        isIntentional: Boolean
    ) {
        val records = getPickupRecords().toMutableList()

        if (records.isEmpty()) return

        val lastIndex = records.lastIndex

        records[lastIndex] = records[lastIndex].copy(
            isIntentional = isIntentional
        )

        savePickupRecords(records)
    }

    suspend fun syncUnsyncedPickups() = syncMutex.withLock {
        val userId = firebaseAuth.currentUser?.uid
            ?: return@withLock

        val records = getPickupRecords().toMutableList()

        val pendingRecords = records
            .filter {
                !it.isSynced &&
                        (it.userId.isEmpty() || it.userId == userId)
            }
            .take(10)

        if (pendingRecords.size < 10) {
            return@withLock
        }

        val updatedRecords = pendingRecords.map {
            it.copy(userId = userId)
        }

        val intentionalCount = updatedRecords.count {
            it.isIntentional
        }

        val nonIntentionalCount = updatedRecords.size - intentionalCount

        val intentionalPercentage =
            (intentionalCount * 100.0 / updatedRecords.size).toInt()

        val nonIntentionalPercentage =
            (nonIntentionalCount * 100.0 / updatedRecords.size).toInt()

        val batchId = "batch_${System.currentTimeMillis()}"

        val batchData = mapOf(
            "batchId" to batchId,
            "intentionalCount" to intentionalCount,
            "intentionalPercentage" to intentionalPercentage,
            "nonIntentionalCount" to nonIntentionalCount,
            "nonIntentionalPercentage" to nonIntentionalPercentage,
            "pickupsList" to updatedRecords.map {
                it.toFirestore()
            }
        )

        // Primero se guarda el lote en Firestore.
        remoteDatasource.saveBatchSummary(userId, batchData)

        // Solo si Firestore confirma el guardado, se marcan como sincronizados.
        val syncedIds = updatedRecords.map { it.pickupId }.toSet()

        for (index in records.indices) {
            if (records[index].pickupId in syncedIds) {
                records[index] = records[index].copy(
                    userId = userId,
                    isSynced = true
                )
            }
        }

        savePickupRecords(records)
    }
    fun getUnsyncedPickups(): List<PhonePickupModel> {
        return getPickupRecords().filter {
            !it.isSynced
        }
    }

    fun getPickups(): List<Long> {
        val stored = preferences.getString(
            KEY_PICKUPS,
            null
        ) ?: return emptyList()

        return try {
            val array = JSONArray(stored)

            List(array.length()) { index ->
                array.getLong(index)
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun saveTimestamps(
        pickups: List<Long>
    ) {
        val array = JSONArray()

        pickups.forEach {
            array.put(it)
        }

        preferences.edit()
            .putString(
                KEY_PICKUPS,
                array.toString()
            )
            .apply()
    }

    fun getTodayPickups(): List<Long> {
        val today = LocalDate.now()

        return getPickups().filter { timestamp ->
            Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate() == today
        }
    }

    fun clearOldPickups() {
        val todayPickups = getTodayPickups()

        saveTimestamps(todayPickups)

        val todayStart = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val records = getPickupRecords().filter {
            it.timestamp >= todayStart || !it.isSynced
        }

        savePickupRecords(records)
    }

    fun getMindfulCount(): Int {
        return preferences.getInt(
            KEY_MINDFUL_COUNT,
            0
        )
    }

    fun getImpulsiveCount(): Int {
        return preferences.getInt(
            KEY_IMPULSIVE_COUNT,
            0
        )
    }

    fun incrementMindfulCount() {
        preferences.edit()
            .putInt(
                KEY_MINDFUL_COUNT,
                getMindfulCount() + 1
            )
            .apply()
    }

    fun incrementImpulsiveCount() {
        preferences.edit()
            .putInt(
                KEY_IMPULSIVE_COUNT,
                getImpulsiveCount() + 1
            )
            .apply()
    }
}