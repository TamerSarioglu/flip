package com.tamerthedark.flip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamerthedark.flip.ui.theme.FlipTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
    val rotations = remember {
        List(viewModel.cards.size) { Animatable(0f) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Score: ${gameState.score}",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
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

        if (gameState.matchedPairs.size == viewModel.cards.size) {
            Text(
                text = "Congratulations! You won!",
                fontSize = 24.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}