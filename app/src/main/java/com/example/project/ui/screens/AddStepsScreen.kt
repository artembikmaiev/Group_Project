package com.example.project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
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
fun AddStepsScreen(
    navController: NavController
) {
    var weightProgressToday by remember { mutableStateOf("") }
    var newWeightTarget by remember { mutableStateOf("") }
    var distanceProgressToday by remember { mutableStateOf("") }
    var newDistanceTarget by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = Color(0xFF222222)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .padding(16.dp)
                .verticalScroll(scrollState), // Make content scrollable
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
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

            // Weight Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Вага", style = MaterialTheme.typography.titleMedium)
                Text("Ваша ціль:", fontSize = 16.sp)
                Text("68 кілограм", fontSize = 18.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Прогрес за сьогодні (вага):", fontSize = 16.sp)
                BasicTextField(
                    value = weightProgressToday,
                    onValueChange = { weightProgressToday = it },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 16.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    decorationBox = { innerTextField ->
                        if (weightProgressToday.isEmpty()) Text("Впишіть вашу вагу", color = Color.Gray)
                        innerTextField()
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* TODO: Зберегти вагу */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Зберегти вагу", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Якщо ви хочете змінити вашу ціль то напишіть нову:", fontSize = 14.sp, color = Color.Gray)
                BasicTextField(
                    value = newWeightTarget,
                    onValueChange = { newWeightTarget = it },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 16.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    decorationBox = { innerTextField ->
                        if (newWeightTarget.isEmpty()) Text("Впишіть вашу ціль", color = Color.Gray)
                        innerTextField()
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* TODO: Зберегти нову ціль ваги */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Зберегти нову ціль ваги", color = Color.White)
                }
            }

            Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))

            // Distance Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Пройдена дистанція", style = MaterialTheme.typography.titleMedium)
                 Text("Ваша ціль:", fontSize = 16.sp)
                Text("5км", fontSize = 18.sp, color = Color.Gray)
                 Spacer(modifier = Modifier.height(8.dp))
                Text("Прогрес за сьогодні (дистанція):", fontSize = 16.sp)
                BasicTextField(
                    value = distanceProgressToday,
                    onValueChange = { distanceProgressToday = it },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 16.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    decorationBox = { innerTextField ->
                        if (distanceProgressToday.isEmpty()) Text("Впишіть вашу дистанцію", color = Color.Gray)
                        innerTextField()
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* TODO: Зберегти дистанцію */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Зберегти дистанцію", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Якщо ви хочете змінити вашу ціль то напишіть нову:", fontSize = 14.sp, color = Color.Gray)
                BasicTextField(
                    value = newDistanceTarget,
                    onValueChange = { newDistanceTarget = it },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 16.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    decorationBox = { innerTextField ->
                        if (newDistanceTarget.isEmpty()) Text("Впишіть вашу ціль", color = Color.Gray)
                        innerTextField()
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* TODO: Зберегти нову ціль дистанції */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Зберегти нову ціль дистанції", color = Color.White)
                }
            }
        }
    }
} 