package com.dreamteam.breakloop.domain

sealed interface UsageEventsResult {
    data class Success(val usageEvents: List<UsageEvent>) : UsageEventsResult
    data object NoPermission : UsageEventsResult
}