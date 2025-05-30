package com.example.project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.project.ui.components.BottomNavBar
import com.example.project.ui.screens.SharedViewModel
import androidx.compose.runtime.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Calendar
import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.foundation.clickable
import com.example.project.navigation.Screen
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import com.example.project.ui.components.AppHeader

/**
 * Екран профілю користувача.
 * Дозволяє переглядати та редагувати особисті дані (ім'я, пошта, дата народження, стать).
 */
@Composable
fun ProfileScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    // Змінні стану для редагування даних користувача
    var editedUsername by remember { mutableStateOf(sharedViewModel.currentUser?.username ?: "") }
    var editedEmail by remember { mutableStateOf(sharedViewModel.currentUser?.email ?: "") }
    var editedDateOfBirth by remember { mutableStateOf<Date?>(sharedViewModel.currentUser?.dateOfBirth) }
    var isMale by remember { mutableStateOf(sharedViewModel.currentUser?.gender == "Чоловіча") }
    var isFemale by remember { mutableStateOf(sharedViewModel.currentUser?.gender == "Жіноча") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // Налаштування DatePickerDialog для вибору дати народження
    val calendar = Calendar.getInstance()
    editedDateOfBirth?.let { calendar.time = it }
    
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(selectedYear, selectedMonth, selectedDayOfMonth)
            editedDateOfBirth = selectedCalendar.time
        },
        year,
        month,
        dayOfMonth
    )

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
            AppHeader()
            
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(64.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            // Поля для введення та редагування даних користувача
            OutlinedTextField(
                value = editedUsername,
                onValueChange = { editedUsername = it },
                label = { Text("Ім'я користувача") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFF4CAF50), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text("Дані користувача", fontSize = 16.sp, color = Color(0xFF4CAF50))
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = editedEmail,
                        onValueChange = { editedEmail = it },
                        label = { Text("Електронна адреса") },
                        modifier = Modifier.fillMaxWidth()
                            .padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Поле для вибору дати народження з DatePickerDialog
                    OutlinedTextField(
                        value = editedDateOfBirth?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it) } ?: "",
                        onValueChange = { /* Тільки для читання */ },
                        label = { Text("Дата народження") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                            .clickable { datePickerDialog.show() }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Вибір статі за допомогою чекбоксів
                    Text("Стать", fontSize = 16.sp, color = Color.Black)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isMale,
                            onCheckedChange = { isMale = it; if (it) isFemale = false },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4CAF50))
                        )
                        Text("Чоловіча")

                        Spacer(modifier = Modifier.width(16.dp))

                        Checkbox(
                            checked = isFemale,
                            onCheckedChange = { isFemale = it; if (it) isMale = false },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4CAF50))
                        )
                        Text("Жіноча")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Відображення рівня активності на основі пройденої дистанції
                    Text("Рівень активності: ${sharedViewModel.calculateActivityLevel(sharedViewModel.currentDistance.toIntOrNull() ?: 0)}", fontSize = 16.sp, color = Color.Black)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Кнопка для збереження змін даних користувача
            Button(
                onClick = {
                    scope.launch {
                        val gender = when {
                            isMale -> "Чоловіча"
                            isFemale -> "Жіноча"
                            else -> null
                        }
                        sharedViewModel.updateCurrentUser(editedUsername, editedEmail, editedDateOfBirth, gender)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Зберегти зміни", color = Color.White)
            }

            // Кнопка для виходу з акаунту
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    sharedViewModel.logoutUser(context)
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Вийти з акаунту", color = Color.White)
            }
        }
    }
} 