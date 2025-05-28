package com.example.project.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.Date
import java.util.Calendar

@Dao
interface DailyProgressDao {
    @Query("SELECT * FROM daily_progress WHERE userId = :userId AND date(date/1000, 'unixepoch') = date(:date/1000, 'unixepoch')")
    suspend fun getDailyProgress(userId: Int, date: Date): DailyProgress?

    @Query("SELECT * FROM daily_progress WHERE date(date/1000, 'unixepoch') = date(:date/1000, 'unixepoch')")
    suspend fun getDailyProgressByDate(date: Date): DailyProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyProgress(dailyProgress: DailyProgress)

    @Update
    suspend fun updateDailyProgress(dailyProgress: DailyProgress)

    @Query("SELECT * FROM daily_progress WHERE userId = :userId ORDER BY date DESC")
    fun getAllDailyProgress(userId: Int): List<DailyProgress>

    @Delete
    suspend fun deleteDailyProgress(dailyProgress: DailyProgress)

    @Query("DELETE FROM daily_progress WHERE userId = :userId")
    suspend fun deleteAllDailyProgress(userId: Int)
} 