package com.dreamteam.breakloop.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dreamteam.breakloop.data.local.dao.AppUsageDao
import com.dreamteam.breakloop.data.local.dao.DailyUsageStatsDao
import com.dreamteam.breakloop.data.local.entity.AppUsageEntity
import com.dreamteam.breakloop.data.local.entity.DailyUsageStatsEntity


@Database(
    entities = [DailyUsageStatsEntity::class, AppUsageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dailyUsageStatsDao(): DailyUsageStatsDao
    abstract fun appUsageDao(): AppUsageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "breakloop.db"
                ).build().also {
                    INSTANCE = it
                }
                instance
            }
        }
    }
}
