package com.dreamteam.breakloop.domain.repository

import com.dreamteam.breakloop.domain.ContextSnapshot

interface ContextProvider {
    suspend fun getSnapshot(availableMin: Int): ContextSnapshot
}