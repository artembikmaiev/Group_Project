package com.example.project.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "WellMinder",
            style = MaterialTheme.typography.headlineSmall
        )
        Divider(
            color = Color(0xFF4CAF50),
            thickness = 4.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
} 