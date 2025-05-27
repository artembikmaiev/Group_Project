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

@Composable
fun ProfileScreen(navController: NavController) {
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
            Spacer(modifier = Modifier.height(16.dp))
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
            Text("Користувач1234", fontSize = 18.sp, color = Color.Gray)
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
                    Text("Ім'я та прізвище: Користувач 1234")
                    Text("Електронна адреса: koristuvach@gmail.com")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { /* TODO: Вийти з акаунту */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Вийти з акаунту", color = Color.White)
            }
        }
    }
} 