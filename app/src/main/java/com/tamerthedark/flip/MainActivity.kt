package com.tamerthedark.flip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamerthedark.flip.ui.theme.FlipTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlipTheme {
                Scaffold(modifier = Modifier.fillMaxSize(),) { innerPadding ->
                    MemoryGame()
                }
            }
        }
    }
}

@Composable
fun MemoryGame() {
    val viewModel = remember { MemoryGameViewModel() }
    val gameState by viewModel.gameState.collectAsState()
    val scope = rememberCoroutineScope()
    val shakeOffset = remember { Animatable(0f) }
    
    if (gameState.selectedDifficulty == null) {
        DifficultyDialog { difficulty ->
            viewModel.setDifficulty(difficulty)
        }
    } else {
        val rotations = remember(viewModel.cards) {
            List(viewModel.cards.size) { Animatable(0f) }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Score: ${gameState.score}",
                    fontSize = 24.sp
                )
                Text(
                    text = "Time: ${gameState.remainingTime}s",
                    fontSize = 24.sp,
                    color = if (gameState.remainingTime <= 10) MaterialTheme.colorScheme.error 
                           else MaterialTheme.colorScheme.onSurface
                )
            }

            gameState.selectedDifficulty?.let { difficulty ->
                LazyVerticalGrid(
                    columns = GridCells.Fixed(difficulty.gridSize),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.cards.size) { index ->
                        MemoryCard(
                            card = viewModel.cards[index],
                            rotation = rotations[index].value,
                            isShaking = gameState.shakingCards.contains(index),
                            shakeOffset = shakeOffset.value,
                            enabled = !gameState.isAnimating &&
                                    !gameState.flippedIndices.contains(index) &&
                                    !gameState.matchedPairs.contains(index) &&
                                    gameState.canClick &&
                                    !gameState.isComparing,
                            onCardClick = {
                                viewModel.onCardClick(index, scope, rotations, shakeOffset)
                            }
                        )
                    }
                }
            }

            if (gameState.isGameOver && gameState.remainingTime == 0) {
                Text(
                    text = "Time's Up!",
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            } else if (gameState.matchedPairs.size == viewModel.cards.size) {
                Text(
                    text = "Congratulations! You won!",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )
                viewModel.onCleared()
            }
        }
    }
}