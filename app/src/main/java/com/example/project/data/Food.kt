package com.example.project.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Модель даних продукту харчування
 * Використовується для зберігання інформації про продукти та їх калорійність
 */
@Entity(tableName = "food_items")
data class Food(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String, // Назва продукту
    val calories: Int // Кількість калорій на 100 грам
) 