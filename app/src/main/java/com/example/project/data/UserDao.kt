package com.example.project.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * Data Access Object для роботи з користувачами
 * Надає методи для реєстрації, авторизації та оновлення даних користувача
 */
@Dao
interface UserDao {
    /**
     * Додавання нового користувача
     * @param user Дані користувача для реєстрації
     */
    @Insert
    suspend fun insertUser(user: User)

    /**
     * Авторизація користувача
     * @param username Логін користувача
     * @param password Пароль користувача
     * @return Дані користувача або null, якщо авторизація не вдалася
     */
    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): User?

    /**
     * Перевірка наявності користувача за логіном
     * @param username Логін для перевірки
     * @return Дані користувача або null, якщо користувач не знайдений
     */
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    /**
     * Перевірка наявності користувача за email
     * @param email Email для перевірки
     * @return Дані користувача або null, якщо користувач не знайдений
     */
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    /**
     * Оновлення даних користувача
     * @param user Оновлені дані користувача
     */
    @Update
    suspend fun updateUser(user: User)

    /**
     * Отримання користувача за ID
     * @param userId ID користувача
     * @return Дані користувача або null, якщо користувач не знайдений
     */
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): User?
} 