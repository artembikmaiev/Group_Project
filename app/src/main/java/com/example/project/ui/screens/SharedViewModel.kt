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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow

class SharedViewModel(
    private val database: AppDatabase,
    private val context: Context
) : ViewModel() {
    private val dailyProgressDao = database.dailyProgressDao()
    private val userDao = database.userDao()

    // StateFlow для щоденного прогресу
    private val _dailyProgress = MutableStateFlow<DailyProgress?>(null)
    val dailyProgress: StateFlow<DailyProgress?> = _dailyProgress

    var selectedFoods by mutableStateOf(listOf<ConsumedFood>())
        private set

    // Виносимо змінні стану для цілей та поточних значень
    private var _weightTarget = mutableStateOf("0")
    val weightTarget: String
        get() = _weightTarget.value

    private var _distanceTarget = mutableStateOf("5")
    val distanceTarget: String
        get() = _distanceTarget.value

    private var _waterTarget = mutableStateOf("2")
    val waterTarget: String
        get() = _waterTarget.value

    private var _caloriesTarget = mutableStateOf("2000")
    val caloriesTarget: String
        get() = _caloriesTarget.value

    // Отримуємо поточні значення з dailyProgress StateFlow для UI
    val currentWeight: String
        get() = _dailyProgress.value?.weightProgress?.toString() ?: "0"

    val currentDistance: String
        get() = _dailyProgress.value?.distanceProgress?.toString() ?: "0"

    val waterProgress: String
        get() = _dailyProgress.value?.waterProgress?.toString() ?: "0"

    val currentCaloriesProgress: String
        get() = _dailyProgress.value?.caloriesProgress?.toString() ?: "0"

    // Змінна стану для води, що вводиться в UI (не зберігається в DailyProgress напряму)
    private var _waterAmount = mutableStateOf("0")
    val waterAmount: String
        get() = _waterAmount.value

    var currentUser by mutableStateOf<User?>(null)

    init {
        // Завантаження ID останнього користувача при старті
        loadLastUser()

        // Спостерігаємо за поточним користувачем та завантажуємо його дані
        viewModelScope.launch {
            snapshotFlow { currentUser }
                .distinctUntilChanged()
                .collectLatest { user ->
                    if (user != null) {
                        // Завантажуємо щоденний прогрес для поточного користувача
                        loadDailyProgress(user.id)
                        // Оновлюємо цілі при зміні користувача
                        loadUserTargets(user.id)
                    } else {
                        // Якщо користувач вийшов, очищаємо ViewModel state
                        resetViewModelState()
                    }
                }
        }

        // Спостерігаємо за змінами dailyProgress і оновлюємо waterAmount
        viewModelScope.launch {
            _dailyProgress.collectLatest { progress ->
                _waterAmount.value = progress?.waterProgress?.toString() ?: "0"
            }
        }
    }

    // Завантажує ID останнього увійденного користувача з SharedPreferences
    private fun loadLastUser() {
        val userPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val lastUserId = userPrefs.getInt("current_user_id", -1)
        if (lastUserId != -1) {
            viewModelScope.launch {
                // Намагаємося завантажити користувача з бази даних
                currentUser = userDao.getUserById(lastUserId) // Потрібен новий метод в UserDao
            }
        }
    }

    // Очищає ViewModel state до значень за замовчуванням (при виході користувача)
    private fun resetViewModelState() {
        _dailyProgress.value = null // Скидаємо daily progress StateFlow
        _weightTarget.value = "0"
        _distanceTarget.value = "5"
        _waterTarget.value = "2"
        _caloriesTarget.value = "2000"
        _waterAmount.value = "0"
        selectedFoods = emptyList()
    }

    // Завантажує щоденний прогрес для даного користувача за сьогодні
    private fun loadDailyProgress(userId: Int) {
        viewModelScope.launch {
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            // Спостерігаємо за даними з бази даних за сьогодні
            dailyProgressDao.getDailyProgressFlow(userId, today)
                .collectLatest { progress ->
                    _dailyProgress.value = progress
                }
        }
    }

    // Завантажує цілі користувача (можливо, з останнього запису або з окремого місця)
    // Для простоти поки що візьмемо з DailyProgress, якщо є
    private fun loadUserTargets(userId: Int) {
         viewModelScope.launch {
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
             val dailyProgress = dailyProgressDao.getDailyProgress(userId, today) // Викликаємо suspend версію

             dailyProgress?.let {
                 _weightTarget.value = it.weightTarget.toString()
                 _distanceTarget.value = it.distanceTarget.toString()
                 _waterTarget.value = it.waterTarget.toString()
                 _caloriesTarget.value = it.caloriesTarget.toString()
             } // Якщо запису за сьогодні немає, цілі залишаються дефолтними у ViewModel state
        }
    }

    // Зберігає поточний прогрес та цілі користувача в базу даних
    // Тепер цей метод оновлює існуючий або створює новий запис
    private suspend fun saveDailyProgress() {
        currentUser?.id?.let { userId ->
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            val existingProgress = dailyProgressDao.getDailyProgress(userId, today)

            val progressToSave = existingProgress?.copy(
                distanceTarget = _distanceTarget.value.toIntOrNull() ?: 0,
                distanceProgress = _dailyProgress.value?.distanceProgress ?: 0, // Беремо з StateFlow, null-safe
                waterTarget = _waterTarget.value.toIntOrNull() ?: 0,
                waterProgress = _dailyProgress.value?.waterProgress ?: 0, // Беремо з StateFlow, null-safe
                weightTarget = _weightTarget.value.toIntOrNull() ?: 0,
                weightProgress = _dailyProgress.value?.weightProgress ?: 0, // Беремо з StateFlow, null-safe
                caloriesTarget = _caloriesTarget.value.toIntOrNull() ?: 0,
                caloriesProgress = _dailyProgress.value?.caloriesProgress ?: 0 // Беремо з StateFlow, null-safe
            ) ?: DailyProgress(
                date = today,
                waterProgress = _dailyProgress.value?.waterProgress ?: 0, // Беремо з StateFlow, null-safe
                userId = userId,
                waterTarget = _waterTarget.value.toIntOrNull() ?: 0,
                distanceTarget = _distanceTarget.value.toIntOrNull() ?: 0,
                distanceProgress = _dailyProgress.value?.distanceProgress ?: 0, // Беремо з StateFlow, null-safe
                weightTarget = _weightTarget.value.toIntOrNull() ?: 0,
                weightProgress = _dailyProgress.value?.weightProgress ?: 0, // Беремо з StateFlow, null-safe
                caloriesTarget = _caloriesTarget.value.toIntOrNull() ?: 0,
                caloriesProgress = _dailyProgress.value?.caloriesProgress ?: 0 // Беремо з StateFlow, null-safe
            )

            if (existingProgress == null) {
                 dailyProgressDao.insertDailyProgress(progressToSave)
            } else {
                 dailyProgressDao.updateDailyProgress(progressToSave)
            }
        }
    }

    // Методи оновлення прогресу та цілей тепер оновлюють базу даних

    fun setWeightProgress(weight: String) {
        viewModelScope.launch {
             val currentProgress = _dailyProgress.value
             if(currentProgress != null) {
                 val updated = currentProgress.copy(weightProgress = weight.toIntOrNull() ?: 0)
                 dailyProgressDao.updateDailyProgress(updated)
             } else {
                 // Якщо запису за сьогодні немає, створюємо новий з цим значенням та дефолтними іншими
                  currentUser?.id?.let { userId ->
                      val today = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.time
                      val newProgress = DailyProgress(
                           userId = userId,
                           date = today,
                           weightProgress = weight.toIntOrNull() ?: 0,
                           weightTarget = _weightTarget.value.toIntOrNull() ?: 0,
                           distanceProgress = _dailyProgress.value?.distanceProgress ?: 0, // null-safe
                           distanceTarget = _distanceTarget.value.toIntOrNull() ?: 0,
                           waterProgress = _dailyProgress.value?.waterProgress ?: 0, // null-safe
                           waterTarget = _waterTarget.value.toIntOrNull() ?: 0,
                           caloriesProgress = _dailyProgress.value?.caloriesProgress ?: 0, // null-safe
                           caloriesTarget = _caloriesTarget.value.toIntOrNull() ?: 0
                      )
                      dailyProgressDao.insertDailyProgress(newProgress)
                  }
             }
        }
    }

    fun setWeightTarget(target: String) {
         _weightTarget.value = target // Оновлюємо ViewModel state
         viewModelScope.launch { saveDailyProgress() } // Зберігаємо в базу
    }

    fun setDistanceProgress(distance: String) {
         viewModelScope.launch {
             val currentProgress = _dailyProgress.value
             if(currentProgress != null) {
                 val updated = currentProgress.copy(distanceProgress = distance.toIntOrNull() ?: 0)
                 dailyProgressDao.updateDailyProgress(updated)
             } else {
                 // Якщо запису за сьогодні немає, створюємо новий з цим значенням та дефолтними іншими
                  currentUser?.id?.let { userId ->
                       val today = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.time
                       val newProgress = DailyProgress(
                           userId = userId,
                           date = today,
                           distanceProgress = distance.toIntOrNull() ?: 0,
                           distanceTarget = _distanceTarget.value.toIntOrNull() ?: 0,
                           weightProgress = _dailyProgress.value?.weightProgress ?: 0, // null-safe
                           weightTarget = _weightTarget.value.toIntOrNull() ?: 0,
                           waterProgress = _dailyProgress.value?.waterProgress ?: 0, // null-safe
                           waterTarget = _waterTarget.value.toIntOrNull() ?: 0,
                           caloriesProgress = _dailyProgress.value?.caloriesProgress ?: 0, // null-safe
                           caloriesTarget = _caloriesTarget.value.toIntOrNull() ?: 0
                       )
                       dailyProgressDao.insertDailyProgress(newProgress)
                  }
             }
        }
    }

    fun setDistanceTarget(target: String) {
        _distanceTarget.value = target // Оновлюємо ViewModel state
        viewModelScope.launch { saveDailyProgress() } // Зберігаємо в базу
    }

    fun setWaterProgress(water: String) {
         viewModelScope.launch {
              val currentProgress = _dailyProgress.value
              if(currentProgress != null) {
                  val updated = currentProgress.copy(waterProgress = water.toIntOrNull() ?: 0)
                  dailyProgressDao.updateDailyProgress(updated)
              } else {
                  // Якщо запису за сьогодні немає, створюємо новий з цим значенням та дефолтними іншими
                   currentUser?.id?.let { userId ->
                       val today = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.time
                       val newProgress = DailyProgress(
                            userId = userId,
                            date = today,
                            waterProgress = water.toIntOrNull() ?: 0,
                            waterTarget = _waterTarget.value.toIntOrNull() ?: 0,
                            distanceProgress = _dailyProgress.value?.distanceProgress ?: 0, // null-safe
                            distanceTarget = _distanceTarget.value.toIntOrNull() ?: 0,
                            weightProgress = _dailyProgress.value?.weightProgress ?: 0, // null-safe
                            weightTarget = _weightTarget.value.toIntOrNull() ?: 0,
                            caloriesProgress = _dailyProgress.value?.caloriesProgress ?: 0, // null-safe
                            caloriesTarget = _caloriesTarget.value.toIntOrNull() ?: 0
                       )
                       dailyProgressDao.insertDailyProgress(newProgress)
                   }
              }
         }
    }

    fun setWaterTarget(target: String) {
        _waterTarget.value = target // Оновлюємо ViewModel state
        viewModelScope.launch { saveDailyProgress() } // Зберігаємо в базу
    }

    fun setCaloriesTarget(target: String) {
        _caloriesTarget.value = target // Оновлюємо ViewModel state
        viewModelScope.launch { saveDailyProgress() } // Зберігаємо в базу
    }

    fun setWaterAmount(amount: String) {
        // Це значення для UI вводу, оновлюємо прогрес води
        setWaterProgress(amount)
    }

    // Оновлюємо калорії при додаванні/видаленні їжі
    fun addFood(food: Food, grams: Int) {
        val calories = (food.calories * grams) / 100
        val consumedFood = ConsumedFood(food, grams, calories)
        selectedFoods = selectedFoods + consumedFood
        // Оновлюємо калорії в базі даних
        viewModelScope.launch {
             val currentProgress = _dailyProgress.value
              if(currentProgress != null) {
                 val updated = currentProgress.copy(caloriesProgress = getTotalCalories())
                 dailyProgressDao.updateDailyProgress(updated)
             } else {
                 // Якщо запису за сьогодні немає, створюємо новий з цим значенням та дефолтними іншими
                   currentUser?.id?.let { userId ->
                       val today = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.time
                       val newProgress = DailyProgress(
                            userId = userId,
                            date = today,
                            caloriesProgress = getTotalCalories(),
                            weightProgress = _dailyProgress.value?.weightProgress ?: 0, // null-safe
                           weightTarget = _weightTarget.value.toIntOrNull() ?: 0,
                           distanceProgress = _dailyProgress.value?.distanceProgress ?: 0, // null-safe
                           distanceTarget = _distanceTarget.value.toIntOrNull() ?: 0,
                           waterProgress = _dailyProgress.value?.waterProgress ?: 0, // null-safe
                           waterTarget = _waterTarget.value.toIntOrNull() ?: 0,
                            caloriesTarget = _caloriesTarget.value.toIntOrNull() ?: 0
                       )
                       dailyProgressDao.insertDailyProgress(newProgress)
                   }
              }
        }
    }

    fun removeFood(consumedFood: ConsumedFood) {
        selectedFoods = selectedFoods - consumedFood
        // Оновлюємо калорії в базі даних
         viewModelScope.launch {
              val currentProgress = _dailyProgress.value
              if(currentProgress != null) {
                 val updated = currentProgress.copy(caloriesProgress = getTotalCalories())
                 dailyProgressDao.updateDailyProgress(updated)
              } else {
                  // Якщо запису за сьогодні немає, створюємо новий з цим значенням та дефолтними іншими
                    currentUser?.id?.let { userId ->
                       val today = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.time
                       val newProgress = DailyProgress(
                            userId = userId,
                            date = today,
                            caloriesProgress = getTotalCalories(),
                            weightProgress = _dailyProgress.value?.weightProgress ?: 0, // null-safe
                           weightTarget = _weightTarget.value.toIntOrNull() ?: 0,
                           distanceProgress = _dailyProgress.value?.distanceProgress ?: 0, // null-safe
                           distanceTarget = _distanceTarget.value.toIntOrNull() ?: 0,
                           waterProgress = _dailyProgress.value?.waterProgress ?: 0, // null-safe
                           waterTarget = _waterTarget.value.toIntOrNull() ?: 0,
                            caloriesTarget = _caloriesTarget.value.toIntOrNull() ?: 0
                       )
                       dailyProgressDao.insertDailyProgress(newProgress)
                   }
              }
         }
    }

    fun getTotalCalories(): Int {
        return selectedFoods.sumOf { it.calories }
    }

    // Збереження щоденного прогресу в базу даних (викликається воркером)
    // Цей метод тепер просто викликає saveDailyProgress(), який оновлює/створює запис за сьогодні
    // Назва методу saveDailyProgress() більш відповідає його поточній ролі
    fun saveDailyProgressViaWorker() { // Перейменовуємо для уникнення плутанини
         viewModelScope.launch { saveDailyProgress() }
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
                loadDailyProgress(updatedUser.id) // Оновлюємо завантаження
                loadUserTargets(updatedUser.id) // Оновлюємо цілі
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
        // Скидаємо воду, оновлюючи базу даних
        viewModelScope.launch {
             val currentProgress = _dailyProgress.value
              if(currentProgress != null) {
                 val updated = currentProgress.copy(waterProgress = 0)
                 dailyProgressDao.updateDailyProgress(updated)
              } else {
        currentUser?.id?.let { userId ->
                       val today = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.time
                       val newProgress = DailyProgress(
                            userId = userId,
                            date = today,
                            waterProgress = 0,
                             weightProgress = _dailyProgress.value?.weightProgress ?: 0, // null-safe
                           weightTarget = _weightTarget.value.toIntOrNull() ?: 0,
                           distanceProgress = _dailyProgress.value?.distanceProgress ?: 0, // null-safe
                           distanceTarget = _distanceTarget.value.toIntOrNull() ?: 0,
                            waterTarget = _waterTarget.value.toIntOrNull() ?: 0,
                           caloriesProgress = _dailyProgress.value?.caloriesProgress ?: 0, // null-safe
                           caloriesTarget = _caloriesTarget.value.toIntOrNull() ?: 0
                       )
                       dailyProgressDao.insertDailyProgress(newProgress)
                   }
              }
        }
    }

     fun loginUser(user: User, context: Context) {
         currentUser = user
         // Зберігаємо ID користувача в Shared Preferences для автоматичного входу
         val userPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
         userPrefs.edit().putInt("current_user_id", user.id).apply()
     }

     fun logoutUser(context: Context) {
         currentUser = null
         // Очищаємо ID користувача з Shared Preferences
         val userPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
         userPrefs.edit().remove("current_user_id").apply()
         // ViewModel state скидається через спостереження за currentUser в init
    }
} 