package com.example.project.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Progress : Screen("progress")
    object Food : Screen("food")
    object Statistics : Screen("statistics")
    object Profile : Screen("profile")
}

object NavGraph {
    const val ROOT_ROUTE = "root"
} 