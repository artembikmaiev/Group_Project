package com.example.project.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Модель даних для зберігання щоденного прогресу користувача
 * Зв'язана з таблицею users через зовнішній ключ userId
 * Автоматично видаляється при видаленні користувача (CASCADE)
 */
@Entity(
    tableName = "daily_progress",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("userId")]
)
data class DailyProgress(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int, // ID користувача, до якого належить запис
    val date: Date, // Дата запису прогресу
    val distanceTarget: Int, // Цільова дистанція на день (км)
    val distanceProgress: Int, // Фактично пройдена дистанція (км)
    val waterTarget: Int, // Цільова кількість води (л)
    val waterProgress: Int, // Фактично випита вода (мл)
    val weightTarget: Int, // Цільова вага (кг)
    val weightProgress: Int, // Фактична вага (кг)
    val caloriesTarget: Int, // Цільова кількість калорій
    val caloriesProgress: Int // Фактично спожиті калорії
) 