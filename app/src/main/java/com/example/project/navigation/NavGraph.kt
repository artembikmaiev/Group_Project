package com.example.project.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Activity : Screen("progress")
    object Food : Screen("food")
    object Statistics : Screen("statistics")
    object Profile : Screen("profile")
    object Water : Screen("water")
}

object NavGraph {
    const val ROOT_ROUTE = "root"
} 