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
import com.example.project.ui.screens.SharedViewModel
import kotlinx.coroutines.launch

@Composable
fun AddStepsScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    var weightProgressTodayInput by remember { mutableStateOf(sharedViewModel.currentWeight) }
    var newWeightTargetInput by remember { mutableStateOf(sharedViewModel.weightTarget) }
    var distanceProgressTodayInput by remember { mutableStateOf(sharedViewModel.currentDistance) }
    var newDistanceTargetInput by remember { mutableStateOf(sharedViewModel.distanceTarget) }

    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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
                .padding(16.dp)
                .verticalScroll(scrollState),
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
                Text("${sharedViewModel.weightTarget} кілограм", fontSize = 18.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Прогрес за сьогодні (вага):", fontSize = 16.sp)
                BasicTextField(
                    value = weightProgressTodayInput,
                    onValueChange = { 
                        weightProgressTodayInput = it
                    },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 16.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    decorationBox = { innerTextField ->
                        if (weightProgressTodayInput.isEmpty()) Text("Впишіть вашу вагу", color = Color.Gray)
                        innerTextField()
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { 
                        sharedViewModel.setWeightProgress(weightProgressTodayInput)
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Вага збережена",
                                duration = SnackbarDuration.Short
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Зберегти вагу", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Якщо ви хочете змінити вашу ціль то напишіть нову:", fontSize = 14.sp, color = Color.Gray)
                BasicTextField(
                    value = newWeightTargetInput,
                    onValueChange = { 
                        newWeightTargetInput = it
                    },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 16.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    decorationBox = { innerTextField ->
                        if (newWeightTargetInput.isEmpty()) Text("Впишіть вашу ціль", color = Color.Gray)
                        innerTextField()
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { 
                        sharedViewModel.setWeightTarget(newWeightTargetInput)
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Ціль ваги збережена",
                                duration = SnackbarDuration.Short
                            )
                        }
                    },
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
                Text("${sharedViewModel.distanceTarget}км", fontSize = 18.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Прогрес за сьогодні (дистанція):", fontSize = 16.sp)
                BasicTextField(
                    value = distanceProgressTodayInput,
                    onValueChange = { 
                        distanceProgressTodayInput = it
                    },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 16.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    decorationBox = { innerTextField ->
                        if (distanceProgressTodayInput.isEmpty()) Text("Впишіть вашу дистанцію", color = Color.Gray)
                        innerTextField()
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        sharedViewModel.setDistanceProgress(distanceProgressTodayInput)
                        scope.launch {
                            snackbarHostState.showSnackbar("Кроки збережено!")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Зберегти кроки")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Якщо ви хочете змінити вашу ціль то напишіть нову:", fontSize = 14.sp, color = Color.Gray)
                BasicTextField(
                    value = newDistanceTargetInput,
                    onValueChange = { 
                        newDistanceTargetInput = it
                    },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 16.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    decorationBox = { innerTextField ->
                        if (newDistanceTargetInput.isEmpty()) Text("Впишіть вашу ціль", color = Color.Gray)
                        innerTextField()
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { 
                        sharedViewModel.setDistanceTarget(newDistanceTargetInput)
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Ціль дистанції збережена",
                                duration = SnackbarDuration.Short
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Зберегти нову ціль дистанції", color = Color.White)
                }
            }
        }
    }
} 