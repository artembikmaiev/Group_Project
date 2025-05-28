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

/**
 * Головний екран додатку
 * Відображає основні показники користувача за день:
 * - Пройдену відстань та ціль
 * - Спожиті калорії та денну норму
 * - Кількість випитої води та ціль
 * - Поточну вагу та цільову вагу
 */
@Composable
fun HomeScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    // Отримуємо дані з ViewModel для відображення прогресу
    val currentWeight = sharedViewModel.currentWeight
    val weightTarget = sharedViewModel.weightTarget
    val currentDistance = sharedViewModel.currentDistance
    val distanceTarget = sharedViewModel.distanceTarget
    val totalCalories = sharedViewModel.getTotalCalories() // Загальні спожиті калорії
    val waterProgress = sharedViewModel.currentWaterProgress
    val waterTarget = sharedViewModel.waterTarget

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = Color(0xFF222222) // Колір фону
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White) // Колір фону контенту
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppHeader()

            // Заголовок екрану
            Text(
                text = "Ваші сьогоднішні результати",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Контейнер з чотирма блоками показників
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.45f) // Висота 45% від доступного простору
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(4.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Верхній ряд з двома блоками
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        // Блок з показниками відстані
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .border(1.dp, Color(0xFF4CAF50), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Норма пройденої відстані:", fontSize = 12.sp)
                                Text("${distanceTarget}км", color = Color.Gray, fontSize = 12.sp)
                                Text("Пройдена відстань:", fontSize = 12.sp)
                                Text("${currentDistance}км", color = Color.Gray, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Блок з показниками калорій
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
                                Text("2000 ккал", color = Color.Gray, fontSize = 12.sp)
                                Text("Спожито калорій:", fontSize = 12.sp)
                                Text("${totalCalories} ккал", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Нижній ряд з двома блоками
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        // Блок з показниками води
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

                        Spacer(modifier = Modifier.width(4.dp))

                        // Блок з показниками ваги
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

            // Інформаційний текст про зміну цілей
            Text(
                text = "Якщо ви хочете змінити ваші цілі на день, змініть їх у відповідних секціях додатку",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
} 