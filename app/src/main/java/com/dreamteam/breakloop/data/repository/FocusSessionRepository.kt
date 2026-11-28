package com.dreamteam.breakloop.data.local.repository

import com.dreamteam.breakloop.data.local.dao.FocusSessionDao
import com.dreamteam.breakloop.data.local.entity.FocusSessionEntity
import com.dreamteam.breakloop.data.remote.FocusApi
import com.dreamteam.breakloop.data.remote.dto.FocusSessionRequest
import kotlinx.coroutines.flow.Flow

class FocusSessionRepository(
    private val focusSessionDao: FocusSessionDao,
    private val focusApi: FocusApi
) {

    suspend fun saveSession(
        session: FocusSessionEntity
    ) {
        val sesionExistente = focusSessionDao.getSessionById(session.id)

        val sesionActualizada = session.copy(
            recoveryCount = sesionExistente?.recoveryCount ?: session.recoveryCount,
            lastRecoveredAt = sesionExistente?.lastRecoveredAt ?: session.lastRecoveredAt
        )

        focusSessionDao.insert(sesionActualizada)
    }

    suspend fun getUnsyncedSessions(): List<FocusSessionEntity> {
        return focusSessionDao.getUnsyncedSessions()
    }

    suspend fun markAsSynced(
        id: String
    ) {
        focusSessionDao.markAsSynced(id)
    }

    fun observeSessions(): Flow<List<FocusSessionEntity>> {
        return focusSessionDao.observeSessions()
    }

    suspend fun syncUnsyncedSessions() {
        val sessions = focusSessionDao.getUnsyncedSessions()

        for (session in sessions) {
            val request = FocusSessionRequest(
                id = session.id,
                startTime = session.startTime,
                duration = session.duration,
                type = session.type,
                status = session.status,
                xpEarned = session.xpEarned,
                recoveryCount = session.recoveryCount,
                lastRecoveredAt = session.lastRecoveredAt
            )

            val response = focusApi.saveSession(
                uid = session.uid,
                session = request
            )

            if (!response.isSuccessful) {
                throw IllegalStateException(
                    "Could not sync session ${session.id}. " +
                            "HTTP ${response.code()}"
                )
            }

            focusSessionDao.markAsSynced(
                session.id
            )
        }
    }

    //Sprint 4
    suspend fun updateSessionProgress(
        id: String,
        elapsedSeconds: Int,
        remainingSeconds: Int,
        isPaused: Boolean,
        lastUpdatedAt: Long,
        status: String
    ) {
        focusSessionDao.updateSessionProgress(
            id = id,
            elapsedSeconds = elapsedSeconds,
            remainingSeconds = remainingSeconds,
            isPaused = isPaused,
            lastUpdatedAt = lastUpdatedAt,
            status = status
        )
    }

    suspend fun getActiveSession(uid: String): FocusSessionEntity? {
        return focusSessionDao.getActiveSession(uid)
    }

    suspend fun getPendingSessions(uid: String): List<FocusSessionEntity> {
        return focusSessionDao.getPendingSessions(uid)
    }

    //Sprint 4: Registrar recuperación de una sesión
    suspend fun registerRecovery(
        id: String,
        recoveredAt: Long
    ) {
        focusSessionDao.registerRecovery(
            id = id,
            recoveredAt = recoveredAt
        )
    }


}