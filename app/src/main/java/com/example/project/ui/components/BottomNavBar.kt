package com.example.project.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.project.navigation.Screen

@Composable
fun BottomNavBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val primaryGreen = Color(0xFF4CAF50)
    val inactiveGray = Color(0xFF757575) // Середній сірий колір для неактивних елементів

    NavigationBar(
        modifier = modifier,
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { 
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Головна",
                    tint = if (currentRoute == Screen.Home.route) primaryGreen else inactiveGray
                )
            },
            label = { 
                Text(
                    "Головна",
                    color = if (currentRoute == Screen.Home.route) primaryGreen else inactiveGray
                )
            },
            selected = currentRoute == Screen.Home.route,
            onClick = {
                if (currentRoute != Screen.Home.route) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = primaryGreen,
                unselectedIconColor = inactiveGray,
                selectedTextColor = primaryGreen,
                unselectedTextColor = inactiveGray,
                indicatorColor = Color.White
            )
        )
        NavigationBarItem(
            icon = { 
                Icon(
                    Icons.Default.List,
                    contentDescription = "Прогрес",
                    tint = if (currentRoute == Screen.Activity.route) primaryGreen else inactiveGray
                )
            },
            label = { 
                Text(
                    "Прогрес",
                    color = if (currentRoute == Screen.Activity.route) primaryGreen else inactiveGray
                )
            },
            selected = currentRoute == Screen.Activity.route,
            onClick = {
                if (currentRoute != Screen.Activity.route) {
                    navController.navigate(Screen.Activity.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = primaryGreen,
                unselectedIconColor = inactiveGray,
                selectedTextColor = primaryGreen,
                unselectedTextColor = inactiveGray,
                indicatorColor = Color.White
            )
        )
        NavigationBarItem(
            icon = { 
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = "Їжа",
                    tint = if (currentRoute == Screen.Food.route) primaryGreen else inactiveGray
                )
            },
            label = { 
                Text(
                    "Їжа",
                    color = if (currentRoute == Screen.Food.route) primaryGreen else inactiveGray
                )
            },
            selected = currentRoute == Screen.Food.route,
            onClick = {
                if (currentRoute != Screen.Food.route) {
                    navController.navigate(Screen.Food.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = primaryGreen,
                unselectedIconColor = inactiveGray,
                selectedTextColor = primaryGreen,
                unselectedTextColor = inactiveGray,
                indicatorColor = Color.White
            )
        )
        NavigationBarItem(
            icon = { 
                Icon(
                    Icons.Default.List,
                    contentDescription = "Статистика",
                    tint = if (currentRoute == Screen.Statistics.route) primaryGreen else inactiveGray
                )
            },
            label = { 
                Text(
                    "Статистика",
                    color = if (currentRoute == Screen.Statistics.route) primaryGreen else inactiveGray
                )
            },
            selected = currentRoute == Screen.Statistics.route,
            onClick = {
                if (currentRoute != Screen.Statistics.route) {
                    navController.navigate(Screen.Statistics.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = primaryGreen,
                unselectedIconColor = inactiveGray,
                selectedTextColor = primaryGreen,
                unselectedTextColor = inactiveGray,
                indicatorColor = Color.White
            )
        )
        NavigationBarItem(
            icon = { 
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Користувач",
                    tint = if (currentRoute == Screen.Profile.route) primaryGreen else inactiveGray
                )
            },
            label = { 
                Text(
                    "Користувач",
                    color = if (currentRoute == Screen.Profile.route) primaryGreen else inactiveGray
                )
            },
            selected = currentRoute == Screen.Profile.route,
            onClick = {
                if (currentRoute != Screen.Profile.route) {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = primaryGreen,
                unselectedIconColor = inactiveGray,
                selectedTextColor = primaryGreen,
                unselectedTextColor = inactiveGray,
                indicatorColor = Color.White
            )
        )
    }
} 