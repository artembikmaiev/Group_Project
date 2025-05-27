package com.example.project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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

@Composable
fun FoodScreen(navController: NavController) {
    var search by remember { mutableStateOf("") }
    val products = listOf(
        "Апельсин" to "Калорійність на 100г: 47 ккал",
        "Апельсиновий сік" to "Калорійність на 100г: 47 ккал",
        "Апельсиновий кекс" to "Калорійність на 100г: 150 ккал",
        "Апельсиновий пюре" to "Калорійність на 100г: 160 ккал",
        "Апельсинове морозиво" to "Калорійність на 100г: 100 ккал"
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
                    if (search.isEmpty()) Text("Апельсин", color = Color.Gray)
                    innerTextField()
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFF4CAF50), RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                products.forEach { (name, kcal) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(name, fontSize = 16.sp)
                        Text(kcal, fontSize = 14.sp, color = Color.Gray)
                    }
                    Divider()
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { /* TODO: Додати продукт */ }
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Додати продукт", color = Color(0xFF4CAF50), fontSize = 18.sp)
            }
        }
    }
} 