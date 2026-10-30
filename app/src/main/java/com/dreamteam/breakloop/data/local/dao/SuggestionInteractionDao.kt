package com.dreamteam.breakloop.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.dreamteam.breakloop.data.local.entity.SuggestionInteractionEntity

@Dao
interface SuggestionInteractionDao {
    @Upsert(entity = SuggestionInteractionEntity::class)
    suspend fun upsert(entity: SuggestionInteractionEntity)

    @Query("SELECT * FROM suggestion_interaction WHERE id = :id")
    suspend fun getById(id: String): SuggestionInteractionEntity?

    @Query("SELECT * FROM suggestion_interaction WHERE synced = 0 LIMIT :limit")
    suspend fun getUnsynced(limit: Int): List<SuggestionInteractionEntity?>

    @Query("UPDATE suggestion_interaction SET synced = 1, updatedAt = :updatedAT WHERE id = :id")
    suspend fun markSynced(id: String, updatedAT: Long)
}