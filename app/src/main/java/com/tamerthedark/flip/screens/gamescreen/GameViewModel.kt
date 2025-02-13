package com.tamerthedark.flip.screens.gamescreen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import com.tamerthedark.flip.DifficultyLevel
import com.tamerthedark.flip.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@HiltViewModel
class MemoryGameViewModel @Inject constructor() : ViewModel() {
    private val allEmojis = listOf(
        "🎮", "🎲", "🎯", "🎨", "🎭", "🎪", "🎫", "🎰",
        "🎸", "🎺", "🎻", "🎹", "🎼", "🎧", "🎤", "🎬",
        "🎨", "🎭", "🎪", "🎫", "🎰", "🎲", "🎯", "🎮",
        "🎸", "🎺", "🎻", "🎹", "🎼", "🎧", "🎤", "🎬",
        "🌟", "🌙", "⭐", "☀️", "🌈", "☁️", "⚡", "❄️",
        "🌸", "🌺", "🌷", "🌹", "🌻", "🍀", "🌿", "🍃"
    )

    private var currentEmojis: List<String> = emptyList()
    var cards: List<String> = emptyList()
        private set

    private val _gameState = MutableStateFlow(GameState())
    val gameState = _gameState.asStateFlow()

    private var timerJob: Job? = null

    private var savedTimeRemaining: Int = 0
    private var wasGameRunning: Boolean = false

    fun setDifficulty(difficulty: DifficultyLevel) {
        currentEmojis = allEmojis.take(difficulty.iconCount)
        cards = (currentEmojis + currentEmojis).shuffled()
        _gameState.value = _gameState.value.copy(
            selectedDifficulty = difficulty,
            remainingTime = difficulty.timeLimit
        )
        startTimer()
    }

    fun pauseGame() {
        if (_gameState.value.remainingTime > 0 && !_gameState.value.isGameOver) {
            wasGameRunning = true
            savedTimeRemaining = _gameState.value.remainingTime
            timerJob?.cancel()
        }
    }

    fun resumeGame() {
        if (wasGameRunning && !_gameState.value.isGameOver) {
            _gameState.value = _gameState.value.copy(
                remainingTime = savedTimeRemaining
            )
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = CoroutineScope(Dispatchers.Main).launch {
            while (_gameState.value.remainingTime > 0 && !_gameState.value.isGameOver) {
                delay(1000)
                _gameState.value = _gameState.value.copy(
                    remainingTime = _gameState.value.remainingTime - 1
                )

                if (_gameState.value.remainingTime == 0) {
                    _gameState.value = _gameState.value.copy(
                        isGameOver = true,
                        canClick = false
                    )
                }
            }
        }
    }

    // Add cleanup method
    public override fun onCleared() {
        timerJob?.cancel()
    }

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
            currentState.isComparing
        ) return

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

    fun retryCurrentLevel() {
        val currentDifficulty = _gameState.value.selectedDifficulty
        currentDifficulty?.let { difficulty ->
            cards = (currentEmojis + currentEmojis).shuffled()
            _gameState.value = GameState(
                selectedDifficulty = difficulty,
                remainingTime = difficulty.timeLimit
            )
            startTimer()
        }
    }

    fun resetGame() {
        timerJob?.cancel()
        _gameState.value = GameState()
    }
}