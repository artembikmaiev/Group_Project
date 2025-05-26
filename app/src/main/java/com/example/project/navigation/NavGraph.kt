package com.example.project.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object AddProduct : Screen("addProduct")
    object AddSteps : Screen("addSteps")
    object AddActivity : Screen("addActivity")
}

object NavGraph {
    const val ROOT_ROUTE = "root"
} 