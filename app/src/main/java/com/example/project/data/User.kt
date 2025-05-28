package com.example.project.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Модель даних користувача системи
 * Зберігає основну інформацію про користувача та його облікові дані
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String, // Логін користувача
    val password: String, // Захешований пароль
    val email: String, // Email для відновлення доступу
    val dateOfBirth: Date? = null, // Дата народження (опціонально)
    val gender: String? = null // Стать (опціонально)
) 