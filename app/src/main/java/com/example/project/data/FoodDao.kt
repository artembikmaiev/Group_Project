package com.example.project.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object для роботи з продуктами
 * Надає методи для додавання та отримання списку продуктів
 */
@Dao
interface FoodDao {
    /**
     * Додавання нового продукту
     * @param food Дані продукту для додавання
     */
    @Insert
    suspend fun insertFood(food: Food)

    /**
     * Отримання всіх продуктів
     * @return Flow зі списком продуктів, відсортованих за назвою
     */
    @Query("SELECT * FROM food_items ORDER BY name ASC")
    fun getAllFood(): Flow<List<Food>>

    /**
     * Видалення всіх продуктів з бази даних
     */
    @Query("DELETE FROM food_items")
    suspend fun deleteAllFood()

    /**
     * Додавання списку продуктів
     * @param foodList Список продуктів для додавання
     */
    @Insert
    suspend fun insertAll(foodList: List<Food>)
} 