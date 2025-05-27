package com.example.project.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.Date
import java.util.Calendar

@Dao
interface DailyProgressDao {
    @Query("SELECT * FROM daily_progress WHERE date(date/1000, 'unixepoch') = date(:date/1000, 'unixepoch')")
    suspend fun getDailyProgress(date: Date): DailyProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyProgress(dailyProgress: DailyProgress)

    @Query("SELECT * FROM daily_progress ORDER BY date DESC")
    fun getAllDailyProgress(): List<DailyProgress>

    @Delete
    suspend fun deleteDailyProgress(dailyProgress: DailyProgress)

    @Query("DELETE FROM daily_progress")
    suspend fun deleteAllDailyProgress()
} 