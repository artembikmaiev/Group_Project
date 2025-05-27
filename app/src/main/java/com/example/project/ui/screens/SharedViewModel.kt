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

class SharedViewModel(
    private val database: AppDatabase,
    private val context: Context
) : ViewModel() {
    private val prefs = context.getSharedPreferences("user_progress", Context.MODE_PRIVATE)
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

    fun addFood(food: Food, grams: Int) {
        val calories = (food.calories * grams) / 100 // Розраховую калорійність для порції
        val consumedFood = ConsumedFood(food, grams, calories)
        selectedFoods = selectedFoods + consumedFood
        prefs.edit().putInt("calories_progress", getTotalCalories()).apply()
    }

    fun removeFood(consumedFood: ConsumedFood) {
        selectedFoods = selectedFoods - consumedFood
        prefs.edit().putInt("calories_progress", getTotalCalories()).apply()
    }

    fun setWeightProgress(weight: String) {
        _currentWeight.value = weight // Змінюємо приватну змінну
        prefs.edit().putString("weight_progress", weight).apply()
    }

    fun setWeightTarget(target: String) {
        _weightTarget.value = target // Змінюємо приватну змінну
        prefs.edit().putString("weight_target", target).apply()
    }

    fun setDistanceProgress(distance: String) {
        _currentDistance.value = distance // Змінюємо приватну змінну
        prefs.edit().putString("distance_progress", distance).apply()
    }

    fun setDistanceTarget(target: String) {
        _distanceTarget.value = target // Змінюємо приватну змінну
        prefs.edit().putString("distance_target", target).apply()
    }

    fun setWaterProgress(water: String) {
        _waterProgress.value = water
        prefs.edit().putString("water_progress", water).apply()
    }

    fun setWaterTarget(target: String) {
        _waterTarget.value = target
        prefs.edit().putString("water_target", target).apply()
    }

    fun setCaloriesTarget(target: String) {
        _caloriesTarget.value = target
        prefs.edit().putString("calories_target", target).apply()
    }

    fun getTotalCalories(): Int {
        return selectedFoods.sumOf { it.calories }
    }

    fun saveDailyProgress() {
        viewModelScope.launch {
            val dailyProgress = DailyProgress(
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
    }

    fun getDailyProgress(date: Date): Flow<DailyProgress?> = flow {
        val progress = dailyProgressDao.getDailyProgress(date)
        emit(progress)
    }
} 