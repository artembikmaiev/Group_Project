package com.example.project.ui.screens

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project.data.Food
import com.example.project.data.ConsumedFood
import com.example.project.data.DailyProgress
import com.example.project.data.AppDatabase
import kotlinx.coroutines.launch
import java.util.Date
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.example.project.data.User
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.compose.runtime.snapshotFlow

class SharedViewModel(
    private val database: AppDatabase,
    private val context: Context
) : ViewModel() {
    private val prefs = context.getSharedPreferences("user_progress", Context.MODE_PRIVATE)
    private val userPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val dailyProgressDao = database.dailyProgressDao()

    var selectedFoods by mutableStateOf(listOf<ConsumedFood>()) // Список ConsumedFood
        private set

    private var _currentWeight = mutableStateOf(prefs.getString("weight_progress", "70") ?: "70") // Приватна змінна стану
    val currentWeight: String
        get() = _currentWeight.value // Публічний доступ тільки для читання

    private var _weightTarget = mutableStateOf(prefs.getString("weight_target", "68") ?: "68") // Приватна змінна стану
    val weightTarget: String
        get() = _weightTarget.value // Публічний доступ тільки для читання

    private var _currentDistance = mutableStateOf(prefs.getString("distance_progress", "0") ?: "0") // Приватна змінна стану
    val currentDistance: String
        get() = _currentDistance.value // Публічний доступ тільки для читання

    private var _distanceTarget = mutableStateOf(prefs.getString("distance_target", "5") ?: "5") // Приватна змінна стану
    val distanceTarget: String
        get() = _distanceTarget.value // Публічний доступ тільки для читання

    private var _waterProgress = mutableStateOf(prefs.getString("water_progress", "0") ?: "0")
    val waterProgress: String
        get() = _waterProgress.value

    private var _waterTarget = mutableStateOf(prefs.getString("water_target", "2") ?: "2")
    val waterTarget: String
        get() = _waterTarget.value

    private var _caloriesTarget = mutableStateOf(prefs.getString("calories_target", "2000") ?: "2000")
    val caloriesTarget: String
        get() = _caloriesTarget.value

    var currentUser by mutableStateOf<User?>(null)

    init {
        viewModelScope.launch {
            snapshotFlow { currentUser }
                .distinctUntilChanged()
                .collectLatest { user ->
                    user?.let {
                        loadProgressForUser(it.id)
                    } ?: run {
                        _currentWeight.value = "70"
                        _weightTarget.value = "68"
                        _currentDistance.value = "0"
                        _distanceTarget.value = "5"
                        _waterProgress.value = "0"
                        _waterTarget.value = "2"
                        _caloriesTarget.value = "2000"
                        selectedFoods = emptyList()
                    }
                }
        }
    }

    private fun loadProgressForUser(userId: Int) {
        val userProgressPrefs = context.getSharedPreferences("user_progress_$userId", Context.MODE_PRIVATE)
        _currentWeight.value = userProgressPrefs.getString("weight_progress", "70") ?: "70"
        _weightTarget.value = userProgressPrefs.getString("weight_target", "68") ?: "68"
        _currentDistance.value = userProgressPrefs.getString("distance_progress", "0") ?: "0"
        _distanceTarget.value = userProgressPrefs.getString("distance_target", "5") ?: "5"
        _waterProgress.value = userProgressPrefs.getString("water_progress", "0") ?: "0"
        _waterTarget.value = userProgressPrefs.getString("water_target", "2") ?: "2"
        _caloriesTarget.value = userProgressPrefs.getString("calories_target", "2000") ?: "2000"
        selectedFoods = emptyList()
    }

    private fun saveProgressForCurrentUser() {
        currentUser?.id?.let { userId ->
            val userProgressPrefs = context.getSharedPreferences("user_progress_$userId", Context.MODE_PRIVATE)
            userProgressPrefs.edit().apply {
                putString("weight_progress", _currentWeight.value)
                putString("weight_target", _weightTarget.value)
                putString("distance_progress", _currentDistance.value)
                putString("distance_target", _distanceTarget.value)
                putString("water_progress", _waterProgress.value)
                putString("water_target", _waterTarget.value)
                putString("calories_target", _caloriesTarget.value)
                apply()
            }
        }
    }

    fun addFood(food: Food, grams: Int) {
        val calories = (food.calories * grams) / 100 // Розраховую калорійність для порції
        val consumedFood = ConsumedFood(food, grams, calories)
        selectedFoods = selectedFoods + consumedFood
        prefs.edit().putInt("calories_progress", getTotalCalories()).apply()
        saveProgressForCurrentUser()
    }

    fun removeFood(consumedFood: ConsumedFood) {
        selectedFoods = selectedFoods - consumedFood
        prefs.edit().putInt("calories_progress", getTotalCalories()).apply()
        saveProgressForCurrentUser()
    }

    fun setWeightProgress(weight: String) {
        _currentWeight.value = weight // Змінюємо приватну змінну
        prefs.edit().putString("weight_progress", weight).apply()
        saveProgressForCurrentUser()
    }

    fun setWeightTarget(target: String) {
        _weightTarget.value = target // Змінюємо приватну змінну
        prefs.edit().putString("weight_target", target).apply()
        saveProgressForCurrentUser()
    }

    fun setDistanceProgress(distance: String) {
        _currentDistance.value = distance // Змінюємо приватну змінну
        prefs.edit().putString("distance_progress", distance).apply()
        saveProgressForCurrentUser()
    }

    fun setDistanceTarget(target: String) {
        _distanceTarget.value = target // Змінюємо приватну змінну
        prefs.edit().putString("distance_target", target).apply()
        saveProgressForCurrentUser()
    }

    fun setWaterProgress(water: String) {
        _waterProgress.value = water
        prefs.edit().putString("water_progress", water).apply()
        saveProgressForCurrentUser()
    }

    fun setWaterTarget(target: String) {
        _waterTarget.value = target
        prefs.edit().putString("water_target", target).apply()
        saveProgressForCurrentUser()
    }

    fun setCaloriesTarget(target: String) {
        _caloriesTarget.value = target
        prefs.edit().putString("calories_target", target).apply()
        saveProgressForCurrentUser()
    }

    fun getTotalCalories(): Int {
        return selectedFoods.sumOf { it.calories }
    }

    fun saveDailyProgress() {
        viewModelScope.launch {
            currentUser?.id?.let { userId ->
                val dailyProgress = DailyProgress(
                    userId = userId,
                    date = Date(),
                    distanceTarget = _distanceTarget.value.toIntOrNull() ?: 0,
                    distanceProgress = _currentDistance.value.toIntOrNull() ?: 0,
                    waterTarget = _waterTarget.value.toIntOrNull() ?: 0,
                    waterProgress = _waterProgress.value.toIntOrNull() ?: 0,
                    weightTarget = _weightTarget.value.toIntOrNull() ?: 0,
                    weightProgress = _currentWeight.value.toIntOrNull() ?: 0,
                    caloriesTarget = _caloriesTarget.value.toIntOrNull() ?: 0,
                    caloriesProgress = getTotalCalories()
                )
                dailyProgressDao.insertDailyProgress(dailyProgress)
            }
            currentUser?.id?.let { userId ->
                val userProgressPrefs = context.getSharedPreferences("user_progress_$userId", Context.MODE_PRIVATE)
                userProgressPrefs.edit().apply {
                    putString("weight_progress", "70")
                    putString("distance_progress", "0")
                    putString("water_progress", "0")
                    putString("calories_progress", "0")
                    apply()
                }
                _currentWeight.value = "70"
                _currentDistance.value = "0"
                _waterProgress.value = "0"
                selectedFoods = emptyList()
            }
        }
    }

    fun getDailyProgress(date: Date): Flow<DailyProgress?> = flow {
        currentUser?.id?.let { userId ->
            val progress = dailyProgressDao.getDailyProgress(userId, date)
            emit(progress)
        }
    }

    fun getAllDailyProgress(): Flow<List<DailyProgress>> = flow {
        currentUser?.id?.let { userId ->
            val progressList = dailyProgressDao.getAllDailyProgress(userId)
            emit(progressList)
        } ?: emit(emptyList())
    }

    suspend fun deleteAllDailyProgress() {
        currentUser?.id?.let { userId ->
            dailyProgressDao.deleteAllDailyProgress(userId)
        }
    }

    suspend fun updateCurrentUser(username: String, email: String, dateOfBirth: Date?, gender: String?) {
        currentUser?.let { user ->
            val updatedUser = user.copy(username = username, email = email, dateOfBirth = dateOfBirth, gender = gender)
            database.userDao().updateUser(updatedUser)
            currentUser = updatedUser
        }
    }

    fun calculateActivityLevel(distanceKm: Int): String {
        return when {
            distanceKm < 2 -> "Низька"
            distanceKm >= 2 && distanceKm <= 5 -> "Середня"
            else -> "Висока"
        }
    }

    fun resetWaterProgress() {
        currentUser?.id?.let { userId ->
            val userProgressPrefs = context.getSharedPreferences("user_progress_$userId", Context.MODE_PRIVATE)
            userProgressPrefs.edit().putString("water_progress", "0").apply()
            _waterProgress.value = "0"
        }
    }
} 