package com.dreamteam.breakloop.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dreamteam.breakloop.data.local.dao.ActivityLogDao
import com.dreamteam.breakloop.data.local.dao.AppUsageDao
import com.dreamteam.breakloop.data.local.dao.DailyUsageStatsDao
import com.dreamteam.breakloop.data.local.dao.FlaggedAppDao
import com.dreamteam.breakloop.data.local.dao.FocusSessionDao
import com.dreamteam.breakloop.data.local.dao.FriendProfileDao
import com.dreamteam.breakloop.data.local.dao.PendingProfileViewDao
import com.dreamteam.breakloop.data.local.dao.SuggestionInteractionDao
import com.dreamteam.breakloop.data.local.entity.ActivityLogEntity
import com.dreamteam.breakloop.data.local.entity.AppUsageEntity
import com.dreamteam.breakloop.data.local.entity.DailyUsageStatsEntity
import com.dreamteam.breakloop.data.local.entity.FlaggedAppEntity
import com.dreamteam.breakloop.data.local.entity.FocusSessionEntity
import com.dreamteam.breakloop.data.local.entity.FriendProfileEntity
import com.dreamteam.breakloop.data.local.entity.PendingProfileViewEntity
import com.dreamteam.breakloop.data.local.entity.SuggestionInteractionEntity
import com.dreamteam.breakloop.data.local.dao.UserAchievementDao
import com.dreamteam.breakloop.data.local.entity.UserAchievementEntity

@Database(
    entities = [
        DailyUsageStatsEntity::class,
        AppUsageEntity::class,
        ActivityLogEntity::class,
        FocusSessionEntity::class,
        FlaggedAppEntity::class,
        SuggestionInteractionEntity::class,
        FriendProfileEntity::class,
        PendingProfileViewEntity::class,
        UserAchievementEntity::class
    ],
    version = 11,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dailyUsageStatsDao(): DailyUsageStatsDao
    abstract fun appUsageDao(): AppUsageDao
    abstract fun activityLogDao(): ActivityLogDao

    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun flaggedAppDao(): FlaggedAppDao

    abstract fun suggestionInteractionDao(): SuggestionInteractionDao

    abstract fun friendProfileDao(): FriendProfileDao

    abstract fun pendingProfileViewDao(): PendingProfileViewDao

    abstract fun userAchievementDao(): UserAchievementDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(
            context: Context
        ): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "breakloop.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also {
                        INSTANCE = it
                    }

                instance
            }
        }
    }
}