package com.tamerthedark.flip.presentation.screens.savedscorescreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ScoresScreen() {
    val viewModel: ScoresViewModel = hiltViewModel()
    val scores by viewModel.scores.collectAsState()
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "High Scores",
                style = MaterialTheme.typography.headlineMedium
            )
            // Empty box for symmetry
            Box(modifier = Modifier.width(48.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilterChip(
                selected = selectedDifficulty == null,
                onClick = { viewModel.setDifficultyFilter(null) },
                label = { Text("All") }
            )
            FilterChip(
                selected = selectedDifficulty == "Easy",
                onClick = { viewModel.setDifficultyFilter("Easy") },
                label = { Text("Easy") }
            )
            FilterChip(
                selected = selectedDifficulty == "Medium",
                onClick = { viewModel.setDifficultyFilter("Medium") },
                label = { Text("Medium") }
            )
        }

        // Scores List
        if (scores.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No scores yet!",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(scores) { score ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = score.difficulty,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Score: ${score.score}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
} 