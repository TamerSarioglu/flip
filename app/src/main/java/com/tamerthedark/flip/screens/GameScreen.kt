package com.tamerthedark.flip.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamerthedark.flip.*

@Composable
fun GameScreen(viewModel: MemoryGameViewModel) {
    val gameState by viewModel.gameState.collectAsState()
    val scope = rememberCoroutineScope()
    val shakeOffset = remember { Animatable(0f) }
    
    LaunchedEffect(gameState.matchedPairs.size, viewModel.cards.size) {
        if (gameState.matchedPairs.size == viewModel.cards.size && viewModel.cards.isNotEmpty()) {
            viewModel.onCleared()
        }
    }

    // Add effect to handle screen focus
    DisposableEffect(Unit) {
        onDispose {
            viewModel.pauseGame()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.resumeGame()
    }

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

            LazyVerticalGrid(
                columns = GridCells.Fixed(gameState.selectedDifficulty!!.gridSize),
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

            if (gameState.isGameOver && gameState.remainingTime == 0) {
                RetryDialog(
                    score = gameState.score,
                    onRetry = {
                        viewModel.retryCurrentLevel()
                    },
                    onNewGame = {
                        viewModel.resetGame()
                    }
                )
            } else if (gameState.matchedPairs.size == viewModel.cards.size) {
                Text(
                    text = "Congratulations! You won!",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
} 