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

            // Отримуємо дані прогресу за сьогодні з бази даних
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val today = calendar.time

            val dailyProgress = dailyProgressDao.getDailyProgress(userId, today)

            if (dailyProgress == null) {
                 Log.d("DailyProgressWorker", "No daily progress data found in database for user $userId on $today. Creating default entry.")
                 // Якщо даних за сьогодні немає (наприклад, користувач нічого не вводив)
                 // Створюємо запис за замовчуванням з поточними цілями (якщо вони збережені або дефолтні)
                 // Нам потрібен доступ до цілей. Найпростіше взяти дефолтні або спробувати завантажити останні цілі.
                 // Для простоти візьмемо дефолтні значення для нового запису
                 val defaultDailyProgress = DailyProgress(
                    userId = userId,
                    date = today,
                    weightProgress = 0, // Дефолтні
                    weightTarget = 68, // Дефолтні
                    distanceProgress = 0, // Дефолтні
                    distanceTarget = 5, // Дефолтні
                    waterProgress = 0, // Дефолтні
                    waterTarget = 2, // Дефолтні
                    caloriesProgress = 0, // Дефолтні
                    caloriesTarget = 2000 // Дефолтні
                 )
                 dailyProgressDao.insertDailyProgress(defaultDailyProgress)
                 Log.d("DailyProgressWorker", "Default daily progress entry created for user $userId on $today.")
            } else {
                // Якщо дані за сьогодні є, воркер вже нічого не робить
                // ViewModel зберігає дані в базу в реальному часі
                Log.d("DailyProgressWorker", "Daily progress data already exists in database for user $userId on $today. Worker is done.")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("DailyProgressWorker", "Error saving data", e)
            Result.failure()
        }
    }
} 