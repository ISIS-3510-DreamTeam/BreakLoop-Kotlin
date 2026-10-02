package com.dreamteam.breakloop.data.local.repository

import com.dreamteam.breakloop.data.local.dao.FocusSessionDao
import com.dreamteam.breakloop.data.local.entity.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

class FocusSessionRepository(
    private val focusSessionDao: FocusSessionDao
) {

    suspend fun saveSession(session: FocusSessionEntity) {
        focusSessionDao.insert(session)
    }

    suspend fun getUnsyncedSessions(): List<FocusSessionEntity> {
        return focusSessionDao.getUnsyncedSessions()
    }

    suspend fun markAsSynced(id: String) {
        focusSessionDao.markAsSynced(id)
    }

    fun observeSessions(): Flow<List<FocusSessionEntity>> {
        return focusSessionDao.observeSessions()
    }
}