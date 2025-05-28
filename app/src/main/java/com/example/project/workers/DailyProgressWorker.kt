package com.example.project.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.project.data.AppDatabase
import com.example.project.data.DailyProgress
import java.util.Date
import java.util.Calendar

class DailyProgressWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val currentTime = Date()
        Log.d("DailyProgressWorker", "Starting work at $currentTime")
        
        return try {
            val prefs = context.getSharedPreferences("user_progress", Context.MODE_PRIVATE)
            val database = AppDatabase.getDatabase(context)
            val dailyProgressDao = database.dailyProgressDao()

            // Get current user ID from SharedPreferences
            val userPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val userId = userPrefs.getInt("current_user_id", -1) // -1 is a default value indicating no user

            if (userId == -1) {
                Log.e("DailyProgressWorker", "No user ID found in SharedPreferences. Skipping data save.")
                return Result.failure() // Or Result.success() depending on desired behavior when no user is logged in
            }

            // Отримуємо дані з SharedPreferences
            val weightProgress = prefs.getString("weight_progress", "70")?.toIntOrNull() ?: 70
            val weightTarget = prefs.getString("weight_target", "68")?.toIntOrNull() ?: 68
            val distanceProgress = prefs.getString("distance_progress", "0")?.toIntOrNull() ?: 0
            val distanceTarget = prefs.getString("distance_target", "5")?.toIntOrNull() ?: 5
            val waterProgress = prefs.getString("water_progress", "0")?.toIntOrNull() ?: 0
            val waterTarget = prefs.getString("water_target", "2")?.toIntOrNull() ?: 2
            val caloriesProgress = prefs.getString("calories_progress", "0")?.toIntOrNull() ?: 0
            val caloriesTarget = prefs.getString("calories_target", "2000")?.toIntOrNull() ?: 2000

            Log.d("DailyProgressWorker", """
                Retrieved data:
                - Weight: $weightProgress/$weightTarget
                - Distance: $distanceProgress/$distanceTarget
                - Water: $waterProgress/$waterTarget
                - Calories: $caloriesProgress/$caloriesTarget
            """.trimIndent())

            // Створюємо запис з поточною датою
            val calendar = Calendar.getInstance()
            // Встановлюємо час на 23:59 поточного дня
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

            // Очищаємо прогрес за день
            prefs.edit().apply {
                putString("distance_progress", "0")
                putString("water_progress", "0")
                putString("calories_progress", "0")
                apply()
            }
            Log.d("DailyProgressWorker", "Progress cleared in SharedPreferences for user $userId")

            Result.success()
        } catch (e: Exception) {
            Log.e("DailyProgressWorker", "Error saving data", e)
            Result.failure()
        }
    }
} 