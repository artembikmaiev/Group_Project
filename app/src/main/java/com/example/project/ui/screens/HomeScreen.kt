package com.example.project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.project.ui.components.BottomNavBar
import com.example.project.ui.components.AppHeader
import com.example.project.ui.screens.SharedViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    // Отримую дані з ViewModel
    val currentWeight = sharedViewModel.currentWeight
    val weightTarget = sharedViewModel.weightTarget
    val currentDistance = sharedViewModel.currentDistance
    val distanceTarget = sharedViewModel.distanceTarget
    val totalCalories = sharedViewModel.getTotalCalories() // Отримую загальні калорії
    val waterProgress = sharedViewModel.currentWaterProgress
    val waterTarget = sharedViewModel.waterTarget

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = Color(0xFF222222) // Background color
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White) // Content area background
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppHeader()

            Text(
                text = "Ваші сьогоднішні результати",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.45f) // Adjust the height, e.g., 35% of available height
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(4.dp) // Reduced padding slightly for internal division
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f) // Make rows take equal height
                    ) {
                        // Норма пройденої відстані
                        Box(
                            modifier = Modifier
                                .weight(1f) // Make columns take equal width
                                .fillMaxHeight() // Fill height within the row
                                .border(1.dp, Color(0xFF4CAF50), RoundedCornerShape(8.dp)) // Border for the inner square
                                .padding(8.dp), // Padding inside the square
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Норма пройденої відстані:", fontSize = 12.sp)
                                Text("${distanceTarget}км", color = Color.Gray, fontSize = 12.sp)
                                Text("Пройдена відстань:", fontSize = 12.sp)
                                Text("${currentDistance}км", color = Color.Gray, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(4.dp)) // Space between squares

                        // Норма калорій
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .border(1.dp, Color(0xFF4CAF50), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Норма калорій:", fontSize = 12.sp)
                                Text("2000 ккал", color = Color.Gray, fontSize = 12.sp) // Норма калорій залишається хардкодною
                                Text("Спожито калорій:", fontSize = 12.sp)
                                Text("${totalCalories} ккал", color = Color.Gray, fontSize = 12.sp) // Виводжу спожиті калорії з ViewModel
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp)) // Space between rows

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f) // Make rows take equal height
                    ) {
                        // Норма води за день
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .border(1.dp, Color(0xFF4CAF50), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                             contentAlignment = Alignment.Center
                        ) {
                           Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Норма води за день:", fontSize = 12.sp)
                                Text("${waterTarget} л", color = Color.Gray, fontSize = 12.sp)
                                Text("Випито:", fontSize = 12.sp)
                                Text("${waterProgress} мл", color = Color.Gray, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(4.dp)) // Space between squares

                        // Вага сьогодні
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .border(1.dp, Color(0xFF4CAF50), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                             contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Вага сьогодні:", fontSize = 12.sp)
                                Text("${currentWeight} кг", color = Color.Gray, fontSize = 12.sp)
                                Text("Ваша ціль:", fontSize = 12.sp)
                                Text("${weightTarget} кг", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Якщо ви хочете змінити ваші цілі на день до змініть їх на у відповідних секціях додатку",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
} 