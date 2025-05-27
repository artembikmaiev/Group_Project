package com.example.project.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Insert
    suspend fun insertFood(food: Food)

    @Query("SELECT * FROM food_items ORDER BY name ASC")
    fun getAllFood(): Flow<List<Food>>

    @Query("DELETE FROM food_items")
    suspend fun deleteAllFood()

    @Insert
    suspend fun insertAll(foodList: List<Food>)
} 