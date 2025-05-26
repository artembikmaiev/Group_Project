package com.example.project.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Register",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
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
                .padding(bottom = 16.dp)
        ) {
            Text("Register")
        }

        TextButton(
            onClick = { navController.navigateUp() }
        ) {
            Text("Вже маєте акаунт? Увійдіть")
        }
    }
} 