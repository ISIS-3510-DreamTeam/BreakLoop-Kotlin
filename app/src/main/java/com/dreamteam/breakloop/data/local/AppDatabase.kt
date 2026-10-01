package com.dreamteam.breakloop.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dreamteam.breakloop.data.local.dao.AppUsageDao
import com.dreamteam.breakloop.data.local.dao.DailyUsageDao
import com.dreamteam.breakloop.data.local.entity.AppUsageEntity
import com.dreamteam.breakloop.data.local.entity.DailyUsageStatsEntity

@Database(
    entities = [DailyUsageStatsEntity::class, AppUsageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dailyUsageDao(): DailyUsageDao
    abstract fun appUsageDao(): AppUsageDao
}
