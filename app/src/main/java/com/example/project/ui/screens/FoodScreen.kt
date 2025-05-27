package com.example.project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.project.ui.components.BottomNavBar
import com.example.project.data.AppDatabase
import com.example.project.data.Food
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import com.example.project.ui.screens.SharedViewModel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.text.input.KeyboardType
import com.example.project.data.ConsumedFood

@Composable
fun FoodScreen(
    navController: NavController,
    database: AppDatabase,
    sharedViewModel: SharedViewModel
) {
    var search by remember { mutableStateOf("") }
    val foodDao = remember { database.foodDao() }
    val products by foodDao.getAllFood().collectAsState(initial = emptyList())

    val selectedFoods = sharedViewModel.selectedFoods
    var selectedFood by remember { mutableStateOf<Food?>(null) }

    // Змінні стану для діалогового вікна
    var showDialog by remember { mutableStateOf(false) }
    var gramsInput by remember { mutableStateOf("") }
    var foodToAdd by remember { mutableStateOf<Food?>(null) }

    val filteredProducts = products.filter { it.name.contains(search, ignoreCase = true) }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = Color(0xFF222222),
        snackbarHost = { SnackbarHost(snackbarHostState) } // Якщо залишали SnackbarHost
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
            BasicTextField(
                value = search,
                onValueChange = { search = it },
                singleLine = true,
                textStyle = TextStyle(fontSize = 18.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                decorationBox = { innerTextField ->
                    if (search.isEmpty()) Text("Введіть назву продукту...", color = Color.Gray)
                    innerTextField()
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Контейнер для обох списків та кнопки
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Займає весь доступний простір по висоті
            ) {
                // Верхній список (тепер у контейнері)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Займає половину доступного простору в цьому контейнері
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFF4CAF50), RoundedCornerShape(16.dp))
                        .padding(8.dp)
                ) {
                    items(filteredProducts) { food ->
                        val isSelected = food == selectedFood
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable { 
                                    // При кліку на продукт, зберігаємо його і показуємо діалог
                                    foodToAdd = food
                                    showDialog = true
                                }
                                .background(if (isSelected) Color(0xFFE8F5E9) else Color.White),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(food.name, fontSize = 16.sp)
                            Text("Калорійність на 100г: ${food.calories} ккал", fontSize = 14.sp, color = Color.Gray)
                        }
                        Divider()
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Спожиті продукти", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(8.dp))

                // Нижній список (тепер відображає ConsumedFood без групування)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFF4CAF50), RoundedCornerShape(16.dp))
                        .padding(8.dp)
                ) {
                    if (selectedFoods.isEmpty()) { // Перевіряємо список ConsumedFood
                         item { 
                             Text("Тут з'являться спожиті продукти", color = Color.Gray)
                         }
                    } else {
                        items(selectedFoods) { consumedFood -> // Використовуємо ConsumedFood
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Показуємо назву продукту та грами/калорії
                                Text("${consumedFood.food.name} (${consumedFood.grams}г)", fontSize = 16.sp, modifier = Modifier.weight(1f))
                                Text("Калорійність: ${consumedFood.calories} ккал", fontSize = 14.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Видалити продукт",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier
                                        .size(20.dp) 
                                        .clickable {
                                            sharedViewModel.removeFood(consumedFood) // Видаляємо ConsumedFood
                                        }
                                )
                            }
                            Divider()
                        }
                    }
                }
            }
        }
    }

    // Діалогове вікно для введення грамів
    if (showDialog && foodToAdd != null) {
        AlertDialog(
            onDismissRequest = { 
                showDialog = false
                gramsInput = ""
                foodToAdd = null
            },
            title = { Text("Введіть грами для ${foodToAdd?.name}") },
            text = {
                TextField(
                    value = gramsInput,
                    onValueChange = { gramsInput = it },
                    label = { Text("Грами") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // Тільки цифри
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val grams = gramsInput.toIntOrNull()
                    if (grams != null && grams > 0 && foodToAdd != null) {
                        sharedViewModel.addFood(foodToAdd!!, grams)
                        gramsInput = ""
                        foodToAdd = null
                        showDialog = false
                    } else {
                        // Можна показати помилку, якщо ввід некоректний
                    }
                }) { Text("Додати") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    gramsInput = ""
                    foodToAdd = null
                }) { Text("Скасувати") }
            }
        )
    }
} 