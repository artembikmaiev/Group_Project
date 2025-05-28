package com.example.project.navigation

/**
 * Визначення екранів додатку та їх маршрутів
 * Використовується для навігації між екранами
 */
sealed class Screen(val route: String) {
    object Login : Screen("login") // Екран входу
    object Register : Screen("register") // Екран реєстрації
    object Home : Screen("home") // Головний екран
    object Activity : Screen("progress") // Екран прогресу
    object Food : Screen("food") // Екран харчування
    object Statistics : Screen("statistics") // Екран статистики
    object Profile : Screen("profile") // Екран профілю
    object Water : Screen("water") // Екран споживання води
}

/**
 * Константи для навігації
 */
object NavGraph {
    const val ROOT_ROUTE = "root" // Кореневий маршрут додатку
} 