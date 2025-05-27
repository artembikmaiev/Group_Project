package com.example.project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
fun StatisticsScreen(navController: NavController) {
    var date by remember { mutableStateOf("30.07.2024") }
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
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

            Text("Ваші результати за:", fontSize = 18.sp)
            Text(date, fontSize = 18.sp, color = Color(0xFF4CAF50))
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.30f)
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFF4CAF50), RoundedCornerShape(16.dp))
                    .padding(4.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .border(1.dp, Color(0xFF4CAF50), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Норма пройденої відстані:", fontSize = 12.sp)
                                Text("5км", color = Color.Gray, fontSize = 12.sp)
                                Text("Пройдена відстань:", fontSize = 12.sp)
                                Text("____", color = Color.Gray, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .border(1.dp, Color(0xFF4CAF50), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Норма калорій:", fontSize = 12.sp)
                                Text("2000 ккал", color = Color.Gray, fontSize = 12.sp)
                                Text("Спожито калорій:", fontSize = 12.sp)
                                Text("1300 ккал", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .border(1.dp, Color(0xFF4CAF50), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                             contentAlignment = Alignment.Center
                        ) {
                           Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Норма води за день:", fontSize = 12.sp)
                                Text("2 літри", color = Color.Gray, fontSize = 12.sp)
                                Text("Випито:", fontSize = 12.sp)
                                Text("1.3 літри", color = Color.Gray, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .border(1.dp, Color(0xFF4CAF50), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                             contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Вага сьогодні:", fontSize = 12.sp)
                                Text("70 кг", color = Color.Gray, fontSize = 12.sp)
                                Text("Ваша ціль:", fontSize = 12.sp)
                                Text("68 кг", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Оберіть іншу дату для перегляду попередніх результатів", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            BasicTextField(
                value = date,
                onValueChange = { date = it },
                singleLine = true,
                textStyle = TextStyle(fontSize = 16.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                decorationBox = { innerTextField ->
                    if (date.isEmpty()) Text("Оберіть дату", color = Color.Gray)
                    innerTextField()
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { /* TODO: Зберегти */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Зберегти", color = Color.White)
            }
        }
    }
} 