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
import com.example.project.ui.components.AppHeader

@Composable
fun FoodScreen(
    navController: NavController,
    database: AppDatabase,
    sharedViewModel: SharedViewModel
) {
    // Стан для пошуку продуктів
    var search by remember { mutableStateOf("") }
    val foodDao = remember { database.foodDao() }
    val products by foodDao.getAllFood().collectAsState(initial = emptyList())

    // Список вибраних продуктів для споживання
    val selectedFoods = sharedViewModel.selectedFoods
    var selectedFood by remember { mutableStateOf<Food?>(null) }

    // Стан для діалогового вікна додавання продукту
    var showDialog by remember { mutableStateOf(false) }
    var gramsInput by remember { mutableStateOf("") }
    var foodToAdd by remember { mutableStateOf<Food?>(null) }

    // Фільтруємо продукти за пошуковим запитом
    val filteredProducts = products.filter { it.name.contains(search, ignoreCase = true) }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = Color(0xFF222222),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppHeader()

            // Поле пошуку продуктів
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

            // Контейнер для списків доступних та спожитих продуктів
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Список доступних продуктів з можливістю додавання
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
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

                // Список спожитих продуктів з можливістю видалення
                Text("Спожиті продукти", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFF4CAF50), RoundedCornerShape(16.dp))
                        .padding(8.dp)
                ) {
                    if (selectedFoods.isEmpty()) {
                         item { 
                             Text("Тут з'являться спожиті продукти", color = Color.Gray)
                         }
                    } else {
                        items(selectedFoods) { consumedFood ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
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
                                            sharedViewModel.removeFood(consumedFood)
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

    // Діалогове вікно для введення кількості грамів продукту
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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