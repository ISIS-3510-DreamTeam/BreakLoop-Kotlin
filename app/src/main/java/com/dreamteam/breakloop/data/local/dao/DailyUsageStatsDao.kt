package com.dreamteam.breakloop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dreamteam.breakloop.data.local.entity.DailyUsageStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyUsageStatsDao {

    @Query("SELECT * FROM daily_usage_stats WHERE date = :date")
    fun getDailyUsageStats(date: String): Flow<DailyUsageStatsEntity>

    @Query("SELECT * FROM daily_usage_stats WHERE date = :date")
    fun getDailyUsageStatsOnce(date: String): DailyUsageStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = DailyUsageStatsEntity::class)
    suspend fun saveDailyUsageStats(dailyUsageStats: DailyUsageStatsEntity)

    @Query("SELECT * FROM daily_usage_stats WHERE date >= :dateStart AND date <= :dateEnd ORDER BY date ASC")
    fun getDailyUsageStatsByRange(dateStart: String, dateEnd: String): Flow<List<DailyUsageStatsEntity>>

}