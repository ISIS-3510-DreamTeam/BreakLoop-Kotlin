package com.dreamteam.breakloop.data.local.mapper

import com.dreamteam.breakloop.data.local.entity.AppUsageEntity
import com.dreamteam.breakloop.data.local.entity.DailyUsageStatsEntity
import com.dreamteam.breakloop.domain.AppUsage
import com.dreamteam.breakloop.domain.DailyUsageStats

fun AppUsage.toEntity(): AppUsageEntity{
    return AppUsageEntity(
        date = date,
        packageName = packageName,
        foregroundMs = foregroundMs
    )
}

fun AppUsageEntity.toDomain(): AppUsage{
    return AppUsage(
        date = date,
        packageName = packageName,
        foregroundMs = foregroundMs
    )
}

fun DailyUsageStats.toEntity(): DailyUsageStatsEntity {
    return DailyUsageStatsEntity(
        date = date,
        screenTimeMs = screenTimeMs,
        pickups = pickups,
        unlocks = unlocks,
        isPartial = isPartial,
        updatedAt = updatedAt
    )
}

fun DailyUsageStatsEntity.toDomain(): DailyUsageStats {
    return DailyUsageStats(
        date = date,
        screenTimeMs = screenTimeMs,
        pickups = pickups,
        unlocks = unlocks,
        isPartial = isPartial,
        updatedAt = updatedAt
    )
}