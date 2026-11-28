
package com.dreamteam.breakloop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dreamteam.breakloop.data.local.entity.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: FocusSessionEntity)

    //Excluye sesiones como in progress
    @Query("""
        SELECT * FROM focus_sessions
        WHERE synced = 0
        AND status IN ('COMPLETED', 'ABANDONED')
        ORDER BY startTime ASC
    """)
    suspend fun getUnsyncedSessions(): List<FocusSessionEntity>

    @Query("UPDATE focus_sessions SET synced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Query("SELECT * FROM focus_sessions ORDER BY startTime DESC")
    fun observeSessions(): Flow<List<FocusSessionEntity>>

    @Query("SELECT COALESCE(SUM(xpEarned), 0) FROM focus_sessions WHERE startTime >= :startOfDay")
    fun observeXpSince(startOfDay: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM focus_sessions WHERE uid = :uid AND status = 'COMPLETED'")
    suspend fun countCompleted(uid: String): Int

    //Update sprint 4
    //Actualizar sesión en progreso
    @Query("""
        UPDATE focus_sessions
        SET elapsedSeconds = :elapsedSeconds,
            remainingSeconds = :remainingSeconds,
            isPaused = :isPaused,
            lastUpdatedAt = :lastUpdatedAt,
            status = :status
        WHERE id = :id AND status = 'IN_PROGRESS'
    """)
    suspend fun updateSessionProgress(
        id: String,
        elapsedSeconds: Int,
        remainingSeconds: Int,
        isPaused: Boolean,
        lastUpdatedAt: Long,
        status: String
    )

    //Obtener la sesión en progreso
    @Query("""
        SELECT * FROM focus_sessions
        WHERE uid = :uid AND status = 'IN_PROGRESS'
        ORDER BY startTime DESC
        LIMIT 1
    """)
    suspend fun getActiveSession(uid: String): FocusSessionEntity?

    //Obtener sesiones pendientes de sincronización por usuario
    @Query("SELECT * FROM focus_sessions WHERE uid = :uid AND synced = 0 ORDER BY startTime ASC")
    suspend fun getPendingSessions(uid: String): List<FocusSessionEntity>

    //
}
