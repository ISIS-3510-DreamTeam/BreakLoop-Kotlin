package com.dreamteam.breakloop.domain.sync

import com.dreamteam.breakloop.domain.ContextSnapshot

interface SyncScheduler {
    fun enqueueActivityLogSync()

    fun enqueueUsageSync()

    fun enqueueSuggestionInteractionSync()
}