package com.dreamteam.breakloop.data.local.repository

import com.dreamteam.breakloop.data.local.dao.FocusSessionDao
import com.dreamteam.breakloop.data.local.entity.FocusSessionEntity
import com.dreamteam.breakloop.remote.FocusApi
import com.dreamteam.breakloop.remote.FocusSessionRequest
import kotlinx.coroutines.flow.Flow

class FocusSessionRepository(
    private val focusSessionDao: FocusSessionDao,
    private val focusApi: FocusApi
) {

    suspend fun saveSession(
        session: FocusSessionEntity
    ) {
        focusSessionDao.insert(session)
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
                xpEarned = session.xpEarned
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
}