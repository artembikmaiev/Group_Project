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
import com.example.project.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavController,
    database: AppDatabase
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

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
                text = "Вхід до системи",
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
                        val user = database.userDao().login(username, password)
                        if (user != null) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        } else {
                            errorMessage = "Невірне ім'я користувача або пароль"
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
                Text("Увійти", style = MaterialTheme.typography.titleMedium)
            }

            TextButton(
                onClick = { navController.navigate(Screen.Register.route) },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = primaryGreen
                )
            ) {
                Text("Немає облікового запису? Зареєструватися")
            }
        }
    }
} 