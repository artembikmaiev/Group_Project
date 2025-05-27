package com.example.project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.project.data.AppDatabase
import com.example.project.data.User
import com.example.project.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    navController: NavController,
    database: AppDatabase
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val emailPattern = remember { Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}") }
    val lightGreen = Color(0xFFE8F5E9)
    val primaryGreen = Color(0xFF4CAF50)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(lightGreen)
                .padding(24.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Реєстрація",
                style = MaterialTheme.typography.headlineMedium,
                color = primaryGreen,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Ім'я користувача") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryGreen,
                    unfocusedBorderColor = primaryGreen.copy(alpha = 0.5f),
                    focusedLabelColor = primaryGreen,
                    unfocusedLabelColor = primaryGreen.copy(alpha = 0.7f)
                )
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Електронна пошта") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryGreen,
                    unfocusedBorderColor = primaryGreen.copy(alpha = 0.5f),
                    focusedLabelColor = primaryGreen,
                    unfocusedLabelColor = primaryGreen.copy(alpha = 0.7f)
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryGreen,
                    unfocusedBorderColor = primaryGreen.copy(alpha = 0.5f),
                    focusedLabelColor = primaryGreen,
                    unfocusedLabelColor = primaryGreen.copy(alpha = 0.7f)
                )
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Підтвердження паролю") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryGreen,
                    unfocusedBorderColor = primaryGreen.copy(alpha = 0.5f),
                    focusedLabelColor = primaryGreen,
                    unfocusedLabelColor = primaryGreen.copy(alpha = 0.7f)
                )
            )

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = {
                    scope.launch {
                        if (username.isBlank() || password.isBlank() || email.isBlank() || confirmPassword.isBlank()) {
                            errorMessage = "Будь ласка, заповніть всі поля"
                            return@launch
                        }

                        if (password != confirmPassword) {
                            errorMessage = "Паролі не співпадають"
                            return@launch
                        }

                        if (password.length < 6 || !password.any { it.isDigit() } || !password.any { it.isLetter() }) {
                            errorMessage = "Пароль має бути щонайменше 6 символів і містити літери та цифри"
                            return@launch
                        }

                        if (!emailPattern.matches(email)) {
                            errorMessage = "Невірний формат email"
                            return@launch
                        }

                        val existingUser = database.userDao().getUserByUsername(username)
                        if (existingUser != null) {
                            errorMessage = "Користувач з таким ім'ям вже існує"
                            return@launch
                        }

                        val existingEmail = database.userDao().getUserByEmail(email)
                        if (existingEmail != null) {
                            errorMessage = "Цей email вже зареєстрований"
                            return@launch
                        }

                        val user = User(
                            username = username,
                            password = password,
                            email = email
                        )
                        database.userDao().insertUser(user)
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(8.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryGreen
                )
            ) {
                Text("Зареєструватися", style = MaterialTheme.typography.titleMedium)
            }

            TextButton(
                onClick = { navController.navigateUp() },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = primaryGreen
                )
            ) {
                Text("Вже маєте акаунт? Увійти")
            }
        }
    }
} 