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
import java.util.Calendar

class SharedViewModel(
    private val database: AppDatabase,
    private val context: Context
) : ViewModel() {
    private val dailyProgressDao = database.dailyProgressDao()

    var selectedFoods by mutableStateOf(listOf<ConsumedFood>())
        private set

    // Змінні стану для відстеження прогресу користувача
    private var _currentWeight = mutableStateOf("0")
    val currentWeight: String
        get() = _currentWeight.value

    private var _weightTarget = mutableStateOf("0")
    val weightTarget: String
        get() = _weightTarget.value

    private var _currentDistance = mutableStateOf("0")
    val currentDistance: String
        get() = _currentDistance.value

    private var _distanceTarget = mutableStateOf("5")
    val distanceTarget: String
        get() = _distanceTarget.value

    private var _waterProgress = mutableStateOf("0")
    val waterProgress: String
        get() = _waterProgress.value

    private var _waterTarget = mutableStateOf("2")
    val waterTarget: String
        get() = _waterTarget.value

    private var _caloriesTarget = mutableStateOf("2000")
    val caloriesTarget: String
        get() = _caloriesTarget.value

    private var _waterAmount = mutableStateOf("0")
    val waterAmount: String
        get() = _waterAmount.value

    private var _currentWaterProgress = mutableStateOf("0")
    val currentWaterProgress: String
        get() = _currentWaterProgress.value

    var currentUser by mutableStateOf<User?>(null)

    // Нова змінна стану для поточного прогресу калорій
    private var _currentCaloriesProgress = mutableStateOf("0")
    val currentCaloriesProgress: String
        get() = _currentCaloriesProgress.value

    init {
        viewModelScope.launch {
            snapshotFlow { currentUser }
                .distinctUntilChanged()
                .collectLatest { user ->
                    if (user != null) {
                        // Завантажуємо дані нового користувача
                        loadProgressForUser(user.id)
                    } else {
                        // Якщо користувач вийшов, очищаємо всі дані в ViewModel
                        resetAllProgress()
                    }
                }
        }
    }

    private fun resetAllProgress() {
        // Очищаємо всі змінні стану до значень за замовчуванням у ViewModel
        _currentWeight.value = "0"
        _weightTarget.value = "0"
        _currentDistance.value = "0"
        _distanceTarget.value = "5"
        _waterProgress.value = "0"
        _waterTarget.value = "2"
        _caloriesTarget.value = "2000"
        _currentWaterProgress.value = "0"
        _waterAmount.value = "0"
        _currentCaloriesProgress.value = "0"
        selectedFoods = emptyList()
    }

    private fun loadProgressForUser(userId: Int) {
        viewModelScope.launch {
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            val dailyProgress = dailyProgressDao.getDailyProgress(userId, today)
            
            if (dailyProgress != null) {
                // Якщо є дані за сьогодні, завантажуємо їх
                _currentWeight.value = dailyProgress.weightProgress.toString()
                _weightTarget.value = dailyProgress.weightTarget.toString()
                _currentDistance.value = dailyProgress.distanceProgress.toString()
                _distanceTarget.value = dailyProgress.distanceTarget.toString()
                _waterProgress.value = dailyProgress.waterProgress.toString()
                _waterTarget.value = dailyProgress.waterTarget.toString()
                _caloriesTarget.value = dailyProgress.caloriesTarget.toString()
                // Завантажуємо збережені калорії та воду
                _currentCaloriesProgress.value = dailyProgress.caloriesProgress.toString()
                _currentWaterProgress.value = dailyProgress.waterProgress.toString()
                _waterAmount.value = dailyProgress.waterProgress.toString()
                // Очищаємо список їжі при завантаженні даних за день
                selectedFoods = emptyList()
            } else {
                // Якщо даних за сьогодні немає, встановлюємо значення за замовчуванням у ViewModel
                _currentWeight.value = "0"
                _weightTarget.value = "0"
                _currentDistance.value = "0"
                _distanceTarget.value = "5"
                _waterProgress.value = "0"
                _waterTarget.value = "2"
                _caloriesTarget.value = "2000"
                _currentCaloriesProgress.value = "0"
                _currentWaterProgress.value = "0"
                _waterAmount.value = "0"
                selectedFoods = emptyList() // Скидаємо список їжі, якщо немає даних за сьогодні
            }
        }
    }

    private fun loadCurrentWaterProgress() {
        viewModelScope.launch {
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            currentUser?.id?.let { userId ->
                val dailyProgress = dailyProgressDao.getDailyProgress(userId, today)
                _currentWaterProgress.value = dailyProgress?.waterProgress?.toString() ?: "0"
                _waterAmount.value = dailyProgress?.waterProgress?.toString() ?: "0"
            }
        }
    }

    // Зберігає поточний прогрес та цілі користувача в базу даних
    private fun saveDailyProgressForCurrentUser() {
        currentUser?.id?.let { userId ->
            viewModelScope.launch {
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time

                val dailyProgress = dailyProgressDao.getDailyProgress(userId, today)

                if (dailyProgress != null) {
                    // Оновлюємо існуючий запис
                    val updatedProgress = dailyProgress.copy(
                        distanceTarget = _distanceTarget.value.toIntOrNull() ?: 0,
                        distanceProgress = _currentDistance.value.toIntOrNull() ?: 0,
                        waterTarget = _waterTarget.value.toIntOrNull() ?: 0,
                        waterProgress = _waterProgress.value.toIntOrNull() ?: 0,
                        weightTarget = _weightTarget.value.toIntOrNull() ?: 0,
                        weightProgress = _currentWeight.value.toIntOrNull() ?: 0,
                        caloriesTarget = _caloriesTarget.value.toIntOrNull() ?: 0,
                        caloriesProgress = getTotalCalories()
                    )
                    dailyProgressDao.updateDailyProgress(updatedProgress)
                } else {
                    // Створюємо новий запис
                    val newProgress = DailyProgress(
                        date = today,
                        waterProgress = _waterProgress.value.toIntOrNull() ?: 0,
                        userId = userId,
                        waterTarget = _waterTarget.value.toIntOrNull() ?: 0,
                        distanceTarget = _distanceTarget.value.toIntOrNull() ?: 0,
                        distanceProgress = _currentDistance.value.toIntOrNull() ?: 0,
                        weightTarget = _weightTarget.value.toIntOrNull() ?: 0,
                        weightProgress = _currentWeight.value.toIntOrNull() ?: 0,
                        caloriesTarget = _caloriesTarget.value.toIntOrNull() ?: 0,
                        caloriesProgress = getTotalCalories()
                    )
                    dailyProgressDao.insertDailyProgress(newProgress)
                }
            }
        }
    }

    fun addFood(food: Food, grams: Int) {
        val calories = (food.calories * grams) / 100 // Розраховую калорійність для порції
        val consumedFood = ConsumedFood(food, grams, calories)
        selectedFoods = selectedFoods + consumedFood
        // Оновлюємо змінну стану калорій та зберігаємо в базу даних
        _currentCaloriesProgress.value = getTotalCalories().toString()
        saveDailyProgressForCurrentUser()
    }

    fun removeFood(consumedFood: ConsumedFood) {
        selectedFoods = selectedFoods - consumedFood
        // Оновлюємо змінну стану калорій та зберігаємо в базу даних
        _currentCaloriesProgress.value = getTotalCalories().toString()
        saveDailyProgressForCurrentUser()
    }

    fun setWeightProgress(weight: String) {
        _currentWeight.value = weight
        // Зберігаємо оновлений прогрес ваги в базу даних
        saveDailyProgressForCurrentUser()
    }

    fun setWeightTarget(target: String) {
        _weightTarget.value = target
        // Зберігаємо оновлену ціль ваги в базу даних
        saveDailyProgressForCurrentUser()
    }

    fun setDistanceProgress(distance: String) {
        _currentDistance.value = distance
        // Зберігаємо оновлений прогрес відстані в базу даних
        saveDailyProgressForCurrentUser()
    }

    fun setDistanceTarget(target: String) {
        _distanceTarget.value = target
        // Зберігаємо оновлену ціль відстані в базу даних
        saveDailyProgressForCurrentUser()
    }

    fun setWaterProgress(water: String) {
        _waterProgress.value = water
        // Зберігаємо оновлений прогрес води в базу даних
        saveDailyProgressForCurrentUser()
    }

    fun setWaterTarget(target: String) {
        _waterTarget.value = target
        // Зберігаємо оновлену ціль води в базу даних
        saveDailyProgressForCurrentUser()
    }

    fun setCaloriesTarget(target: String) {
        _caloriesTarget.value = target
        // Зберігаємо оновлену ціль калорій в базу даних
        saveDailyProgressForCurrentUser()
    }

    fun setWaterAmount(amount: String) {
        _waterAmount.value = amount
        _currentWaterProgress.value = amount
        // Зберігаємо оновлений прогрес води в базу даних
        saveDailyProgressForCurrentUser()
    }

    fun getTotalCalories(): Int {
        return selectedFoods.sumOf { it.calories }
    }

    // Збереження щоденного прогресу в базу даних
    // Викликається автоматично о 23:59 через WorkManager
    fun saveDailyProgress() {
        viewModelScope.launch {
            currentUser?.id?.let { userId ->
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time

                // Створюємо запис про прогрес за день
                val dailyProgress = DailyProgress(
                    userId = userId,
                    date = today,
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

                // Не скидаємо прогрес повністю, щоб дані зберігались
                // Скидання ViewModel відбувається при виході з акаунту
            }
        }
    }

    // Отримання прогресу за конкретну дату
    // Використовується для відображення історії в StatisticsScreen
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

    fun updateCurrentUser(username: String, email: String, dateOfBirth: Date?, gender: String?) {
        viewModelScope.launch {
            currentUser?.let { user ->
                val updatedUser = user.copy(username = username, email = email, dateOfBirth = dateOfBirth, gender = gender)
                database.userDao().updateUser(updatedUser)
                currentUser = updatedUser
                // Перезавантажуємо дані користувача після оновлення
                loadProgressForUser(updatedUser.id)
            }
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
        // Скидаємо тільки значення у ViewModel, збереження в базу відбувається в saveDailyProgressForCurrentUser()
        _waterProgress.value = "0"
        _currentWaterProgress.value = "0"
        _waterAmount.value = "0"
        saveDailyProgressForCurrentUser()
    }
} 