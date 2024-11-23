package com.tamerthedark.flip

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Stable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Stable
class MemoryGameViewModel {
    private val emojis = listOf("🎮", "🎲", "🎯", "🎨", "🎭", "🎪", "🎫", "🎰")
    val cards = (emojis + emojis).shuffled()

    private val _gameState = MutableStateFlow(GameState())
    val gameState = _gameState.asStateFlow()

    fun onCardClick(
        index: Int,
        scope: CoroutineScope,
        rotations: List<Animatable<Float, AnimationVector1D>>,
        shakeOffset: Animatable<Float, AnimationVector1D>
    ) {
        val currentState = _gameState.value

        if (!currentState.canClick ||
            currentState.flippedIndices.contains(index) ||
            currentState.matchedPairs.contains(index) ||
            currentState.isAnimating ||
            currentState.isComparing) return

        scope.launch {
            handleCardFlip(index, currentState, rotations, shakeOffset)
        }
    }

    private suspend fun handleCardFlip(
        index: Int,
        currentState: GameState,
        rotations: List<Animatable<Float, AnimationVector1D>>,
        shakeOffset: Animatable<Float, AnimationVector1D>
    ) {
        if (currentState.flippedIndices.isEmpty()) {
            handleFirstCardFlip(index, rotations)
        } else {
            handleSecondCardFlip(index, currentState, rotations, shakeOffset)
        }
    }

    private suspend fun handleFirstCardFlip(
        index: Int,
        rotations: List<Animatable<Float, AnimationVector1D>>
    ) {
        _gameState.value = _gameState.value.copy(isAnimating = true)
        rotations[index].animateTo(180f, tween(400))
        _gameState.value = _gameState.value.copy(
            flippedIndices = setOf(index),
            isAnimating = false
        )
    }

    private suspend fun handleSecondCardFlip(
        index: Int,
        currentState: GameState,
        rotations: List<Animatable<Float, AnimationVector1D>>,
        shakeOffset: Animatable<Float, AnimationVector1D>
    ) {
        _gameState.value = currentState.copy(
            canClick = false,
            isAnimating = true,
            isComparing = true
        )
        val firstIndex = currentState.flippedIndices.first()

        rotations[index].animateTo(180f, tween(400))
        _gameState.value = _gameState.value.copy(
            flippedIndices = currentState.flippedIndices + index
        )

        delay(1000)

        if (cards[firstIndex] == cards[index] && firstIndex != index) {
            handleMatch(firstIndex, index, shakeOffset)
        } else {
            handleMismatch(firstIndex, index, rotations)
        }

        _gameState.value = _gameState.value.copy(
            flippedIndices = emptySet(),
            canClick = true,
            isAnimating = false,
            isComparing = false
        )
    }

    private suspend fun handleMatch(
        firstIndex: Int,
        secondIndex: Int,
        shakeOffset: Animatable<Float, AnimationVector1D>
    ) {
        _gameState.value = _gameState.value.copy(
            shakingCards = setOf(firstIndex, secondIndex)
        )
        shakeOffset.snapTo(0f)

        repeat(3) {
            shakeOffset.animateTo(10f, tween(100))
            shakeOffset.animateTo(-10f, tween(100))
        }
        shakeOffset.animateTo(0f)

        _gameState.value = _gameState.value.copy(
            shakingCards = emptySet(),
            matchedPairs = _gameState.value.matchedPairs + setOf(firstIndex, secondIndex),
            score = _gameState.value.score + 2
        )
    }

    private suspend fun handleMismatch(
        firstIndex: Int,
        secondIndex: Int,
        rotations: List<Animatable<Float, AnimationVector1D>>
    ) {
        coroutineScope {
            launch { rotations[firstIndex].animateTo(0f, tween(400)) }
            launch { rotations[secondIndex].animateTo(0f, tween(400)) }
        }
    }
}