package com.dreamteam.breakloop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.dreamteam.breakloop.data.local.entity.AppUsageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppUsageDao {

    @Query("DELETE FROM app_usage WHERE date = :date")
    suspend fun deleteAppUsageByDate(date: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListAppUsage(appUsageList: List<AppUsageEntity>)

    @Query("SELECT * FROM app_usage WHERE date = :date")
    fun getAppUsageByDate(date: String): Flow<List<AppUsageEntity>>

    @Query("SELECT * FROM app_usage WHERE date > :dateStart AND date < :dateEnd")
    fun getAppUsageByDateRange(dateStart: String, dateEnd: String): Flow<List<AppUsageEntity>>

    @Transaction
    suspend fun replaceAppUsageForDate(date: String, appUsageList: List<AppUsageEntity>) {
        deleteAppUsageByDate(date)
        insertListAppUsage(appUsageList)
    }
}