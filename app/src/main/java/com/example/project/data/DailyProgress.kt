package com.example.project.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "daily_progress")
data class DailyProgress(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Date,
    val distanceTarget: Int, // Норма пройденої відстані
    val distanceProgress: Int, // Пройдена відстань
    val waterTarget: Int, // Норма випитої води
    val waterProgress: Int, // Випита вода
    val weightTarget: Int, // Ціль ваги
    val weightProgress: Int, // Вага сьогодні
    val caloriesTarget: Int, // Норма калорій
    val caloriesProgress: Int // Спожито калорій за сьогодні
) 