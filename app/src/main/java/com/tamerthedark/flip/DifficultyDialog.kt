package com.tamerthedark.flip

import DifficultyLevel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun DifficultyDialog(
    onDifficultySelected: (DifficultyLevel) -> Unit
) {
    val difficulties = listOf(
        DifficultyLevel("Easy", 4, 8, 60),    // 60 seconds for easy
        DifficultyLevel("Medium", 6, 18, 120)  // 120 seconds for medium
    )

    Dialog(onDismissRequest = { }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Select Difficulty",
                    style = MaterialTheme.typography.headlineSmall
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                difficulties.forEach { difficulty ->
                    Button(
                        onClick = { onDifficultySelected(difficulty) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("${difficulty.name} (${difficulty.timeLimit}s)")
                    }
                }
            }
        }
    }
} 