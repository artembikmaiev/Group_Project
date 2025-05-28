package com.example.project.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.project.data.AppDatabase
import com.example.project.data.DailyProgress
import java.util.Date
import java.util.Calendar

/**
 * Воркер для автоматичного збереження щоденного прогресу
 * Запускається о 23:59 кожного дня через WorkManager
 * Зберігає всі показники користувача в базу даних
 */
class DailyProgressWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val currentTime = Date()
        Log.d("DailyProgressWorker", "Starting work at $currentTime")
        
        return try {
            val database = AppDatabase.getDatabase(context)
            val dailyProgressDao = database.dailyProgressDao()

            // Отримуємо ID поточного користувача
            val userPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val userId = userPrefs.getInt("current_user_id", -1)

            if (userId == -1) {
                Log.e("DailyProgressWorker", "No user ID found in SharedPreferences. Skipping data save.")
                return Result.failure()
            }

            // Отримуємо всі показники з індивідуальних SharedPreferences користувача
            val userProgressPrefs = context.getSharedPreferences("user_progress_$userId", Context.MODE_PRIVATE)
            val weightProgress = userProgressPrefs.getString("weight_progress", "70")?.toIntOrNull() ?: 70
            val weightTarget = userProgressPrefs.getString("weight_target", "68")?.toIntOrNull() ?: 68
            val distanceProgress = userProgressPrefs.getString("distance_progress", "0")?.toIntOrNull() ?: 0
            val distanceTarget = userProgressPrefs.getString("distance_target", "5")?.toIntOrNull() ?: 5
            val waterProgress = userProgressPrefs.getString("water_progress", "0")?.toIntOrNull() ?: 0
            val waterTarget = userProgressPrefs.getString("water_target", "2")?.toIntOrNull() ?: 2
            val caloriesProgress = userProgressPrefs.getString("calories_progress", "0")?.toIntOrNull() ?: 0
            val caloriesTarget = userProgressPrefs.getString("calories_target", "2000")?.toIntOrNull() ?: 2000

            Log.d("DailyProgressWorker", """
                Retrieved data for user $userId:
                - Weight: $weightProgress/$weightTarget
                - Distance: $distanceProgress/$distanceTarget
                - Water: $waterProgress/$waterTarget
                - Calories: $caloriesProgress/$caloriesTarget
            """.trimIndent())

            // Встановлюємо час на 23:59 поточного дня
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val date = calendar.time

            Log.d("DailyProgressWorker", "Creating DailyProgress for date: $date")

            val dailyProgress = DailyProgress(
                userId = userId,
                date = date,
                weightProgress = weightProgress,
                weightTarget = weightTarget,
                distanceProgress = distanceProgress,
                distanceTarget = distanceTarget,
                waterProgress = waterProgress,
                waterTarget = waterTarget,
                caloriesProgress = caloriesProgress,
                caloriesTarget = caloriesTarget
            )

            // Зберігаємо дані в базу
            dailyProgressDao.insertDailyProgress(dailyProgress)
            Log.d("DailyProgressWorker", "Data successfully saved to database for user $userId")

            Result.success()
        } catch (e: Exception) {
            Log.e("DailyProgressWorker", "Error saving data", e)
            Result.failure()
        }
    }
} 