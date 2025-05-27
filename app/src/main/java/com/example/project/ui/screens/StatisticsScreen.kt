package com.example.project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.project.ui.components.BottomNavBar
import com.example.project.data.DailyProgress
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    var selectedDate by remember { mutableStateOf(Date()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val dailyProgress by sharedViewModel.getDailyProgress(selectedDate).collectAsState(initial = null)
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate.time)

    // Перевіряємо, чи вибрана сьогоднішня дата
    val isToday = remember(selectedDate) {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
        val selected = Calendar.getInstance().apply {
            time = selectedDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
        today == selected
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = Color(0xFF222222)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WellMinder",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f)
                )
            }
            Divider(color = Color(0xFF4CAF50), thickness = 2.dp, modifier = Modifier.padding(vertical = 8.dp))

            // Кнопка вибору дати
            Button(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text(
                    text = "Дата: ${SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(selectedDate)}",
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Відображення даних
            if (isToday) {
                // Показуємо поточні дані з SharedViewModel
                StatisticsCard(
                    title = "Вага",
                    current = "${sharedViewModel.currentWeight} кг",
                    target = "${sharedViewModel.weightTarget} кг"
                )
                StatisticsCard(
                    title = "Дистанція",
                    current = "${sharedViewModel.currentDistance} км",
                    target = "${sharedViewModel.distanceTarget} км"
                )
                StatisticsCard(
                    title = "Вода",
                    current = "${sharedViewModel.waterProgress} л",
                    target = "${sharedViewModel.waterTarget} л"
                )
                StatisticsCard(
                    title = "Калорії",
                    current = "${sharedViewModel.getTotalCalories()} ккал",
                    target = "${sharedViewModel.caloriesTarget} ккал"
                )
            } else {
                // Показуємо дані з бази даних
                dailyProgress?.let { progress ->
                    StatisticsCard(
                        title = "Вага",
                        current = "${progress.weightProgress} кг",
                        target = "${progress.weightTarget} кг"
                    )
                    StatisticsCard(
                        title = "Дистанція",
                        current = "${progress.distanceProgress} км",
                        target = "${progress.distanceTarget} км"
                    )
                    StatisticsCard(
                        title = "Вода",
                        current = "${progress.waterProgress} л",
                        target = "${progress.waterTarget} л"
                    )
                    StatisticsCard(
                        title = "Калорії",
                        current = "${progress.caloriesProgress} ккал",
                        target = "${progress.caloriesTarget} ккал"
                    )
                } ?: run {
                    Text(
                        text = "Немає даних за вибрану дату",
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = Date(millis)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }
}

@Composable
fun StatisticsCard(
    title: String,
    current: String,
    target: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF4CAF50)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Поточне значення: $current", fontSize = 16.sp)
                Text("Ціль: $target", fontSize = 16.sp, color = Color.Gray)
            }
        }
    }
} 