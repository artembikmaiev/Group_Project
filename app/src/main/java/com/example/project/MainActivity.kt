package com.example.project

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.project.data.AppDatabase
import com.example.project.navigation.Screen
import com.example.project.ui.screens.*
import com.example.project.ui.theme.ProjectTheme
import androidx.work.*
import com.example.project.workers.DailyProgressWorker
import java.util.concurrent.TimeUnit
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d("MainActivity", "Starting WorkManager configuration")
        
        // Налаштовуємо WorkManager для збереження даних о 23:59
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        // Встановлюємо час на 23:59
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // Розраховуємо затримку до наступного запуску
        val now = System.currentTimeMillis()
        val delay = calendar.timeInMillis - now
        val initialDelay = if (delay < 0) {
            // Якщо час вже пройшов, встановлюємо на наступний день
            delay + TimeUnit.DAYS.toMillis(1)
        } else {
            delay
        }

        Log.d("MainActivity", "Initial delay calculated: $initialDelay ms")

        val dailyProgressRequest = PeriodicWorkRequestBuilder<DailyProgressWorker>(
            24, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        Log.d("MainActivity", "WorkRequest created, enqueueing work")

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "daily_progress_save",
            ExistingPeriodicWorkPolicy.UPDATE,
            dailyProgressRequest
        )

        Log.d("MainActivity", "Work enqueued successfully")

        val database = AppDatabase.getDatabase(this)
        
        setContent {
            ProjectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val sharedViewModel: SharedViewModel = viewModel(
                        factory = SharedViewModelFactory(
                            database,
                            this@MainActivity
                        )
                    )
                    
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Login.route
                    ) {
                        composable(Screen.Login.route) {
                            LoginScreen(navController, database, sharedViewModel)
                        }
                        composable(Screen.Register.route) {
                            RegisterScreen(navController, database)
                        }
                        composable(Screen.Home.route) {
                            HomeScreen(navController, sharedViewModel)
                        }
                        composable(Screen.Activity.route) {
                            AddStepsScreen(navController, sharedViewModel)
                        }
                        composable(Screen.Food.route) {
                            FoodScreen(navController, database, sharedViewModel)
                        }
                        composable(Screen.Statistics.route) {
                            StatisticsScreen(navController, sharedViewModel)
                        }
                        composable(Screen.Profile.route) {
                            ProfileScreen(navController, sharedViewModel)
                        }
                    }
                }
            }
        }
    }
}