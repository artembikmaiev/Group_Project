package com.example.project.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.Date
import java.util.Calendar

/**
 * Data Access Object для роботи з щоденним прогресом
 * Надає методи для збереження та отримання даних про прогрес користувача
 */
@Dao
interface DailyProgressDao {
    /**
     * Отримання прогресу користувача за конкретну дату
     * @param userId ID користувача
     * @param date Дата для отримання прогресу
     * @return Запис про прогрес або null, якщо запис не знайдено
     */
    @Query("SELECT * FROM daily_progress WHERE userId = :userId AND date(date/1000, 'unixepoch') = date(:date/1000, 'unixepoch')")
    suspend fun getDailyProgress(userId: Int, date: Date): DailyProgress?

    /**
     * Отримання прогресу за датою для поточного користувача
     * @param date Дата для отримання прогресу
     * @return Запис про прогрес або null, якщо запис не знайдено
     */
    @Query("SELECT * FROM daily_progress WHERE date(date/1000, 'unixepoch') = date(:date/1000, 'unixepoch')")
    suspend fun getDailyProgressByDate(date: Date): DailyProgress?

    /**
     * Отримання потоку даних прогресу користувача за конкретну дату
     * @param userId ID користувача
     * @param date Дата для отримання прогресу
     * @return Flow з записом про прогрес або null
     */
    @Query("SELECT * FROM daily_progress WHERE userId = :userId AND date(date/1000, 'unixepoch') = date(:date/1000, 'unixepoch')")
    fun getDailyProgressFlow(userId: Int, date: Date): Flow<DailyProgress?>

    /**
     * Збереження або оновлення запису про прогрес
     * @param dailyProgress Запис про прогрес для збереження
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyProgress(dailyProgress: DailyProgress)

    /**
     * Оновлення існуючого запису про прогрес
     * @param dailyProgress Оновлений запис про прогрес
     */
    @Update
    suspend fun updateDailyProgress(dailyProgress: DailyProgress)

    /**
     * Отримання всіх записів про прогрес користувача
     * @param userId ID користувача
     * @return Список записів про прогрес, відсортований за датою (спочатку нові)
     */
    @Query("SELECT * FROM daily_progress WHERE userId = :userId ORDER BY date DESC")
    fun getAllDailyProgress(userId: Int): List<DailyProgress>

    /**
     * Видалення конкретного запису про прогрес
     * @param dailyProgress Запис для видалення
     */
    @Delete
    suspend fun deleteDailyProgress(dailyProgress: DailyProgress)

    /**
     * Видалення всіх записів про прогрес користувача
     * @param userId ID користувача
     */
    @Query("DELETE FROM daily_progress WHERE userId = :userId")
    suspend fun deleteAllDailyProgress(userId: Int)
} 