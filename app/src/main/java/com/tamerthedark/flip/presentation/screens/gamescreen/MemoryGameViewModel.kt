package com.tamerthedark.flip.presentation.screens.gamescreen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tamerthedark.flip.domain.model.DifficultyLevel
import com.tamerthedark.flip.domain.model.GameScore
import com.tamerthedark.flip.domain.model.GameState
import com.tamerthedark.flip.domain.model.DialogType
import com.tamerthedark.flip.domain.use_case.GameUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@HiltViewModel
class MemoryGameViewModel @Inject constructor(
    private val gameUseCases: GameUseCases
) : ViewModel() {
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

    init {
        viewModelScope.launch {
            gameUseCases.getGameState().collect { state ->
                _gameState.value = state
                updateDialogState(state)
            }
        }
    }

    private fun updateDialogState(state: GameState) {
        val newDialog = when {
            state.selectedDifficulty == null -> DialogType.Difficulty
            state.isGameOver && state.remainingTime == 0 -> DialogType.Retry
            state.matchedPairs.size == cards.size && cards.isNotEmpty() -> DialogType.Win
            else -> DialogType.None
        }
        
        if (newDialog != state.currentDialog) {
            saveGameState(state.copy(currentDialog = newDialog))
        }
    }

    fun setDifficulty(difficulty: DifficultyLevel) {
        currentEmojis = allEmojis.take(difficulty.iconCount)
        cards = (currentEmojis + currentEmojis).shuffled()
        val newState = GameState(
            selectedDifficulty = difficulty,
            remainingTime = difficulty.timeLimit,
            currentDialog = DialogType.None
        )
        saveGameState(newState)
        startTimer()
    }

    private fun saveGameState(state: GameState) {
        viewModelScope.launch {
            gameUseCases.saveGameState(state)
        }
    }

    private fun saveScore() {
        val currentState = _gameState.value
        currentState.selectedDifficulty?.let { difficulty ->
            viewModelScope.launch {
                gameUseCases.saveScore(
                    GameScore(
                        score = currentState.score,
                        difficulty = difficulty.name
                    )
                )
            }
        }
    }

    fun onCardClick(
        index: Int,
        scope: CoroutineScope,
        rotations: List<Animatable<Float, AnimationVector1D>>,
        shakeOffset: Animatable<Float, AnimationVector1D>
    ) {
        val currentState = _gameState.value
        if (!currentState.canClick || currentState.flippedIndices.contains(index)) return

        scope.launch {
            rotations[index].animateTo(180f, tween(500))
        }

        val flippedIndices = currentState.flippedIndices.toMutableSet()
        flippedIndices.add(index)

        val newState = currentState.copy(
            flippedIndices = flippedIndices,
            isComparing = flippedIndices.size == 2
        )
        saveGameState(newState)

        if (flippedIndices.size == 2) {
            compareCards(flippedIndices.toList(), scope, rotations, shakeOffset)
        }
    }

    private fun compareCards(
        indices: List<Int>,
        scope: CoroutineScope,
        rotations: List<Animatable<Float, AnimationVector1D>>,
        shakeOffset: Animatable<Float, AnimationVector1D>
    ) {
        viewModelScope.launch {
            val currentState = _gameState.value
            val (firstIndex, secondIndex) = indices
            val isMatch = cards[firstIndex] == cards[secondIndex]

            if (isMatch) {
                val shakingCards = indices.toSet()
                saveGameState(currentState.copy(shakingCards = shakingCards))
                
                scope.launch {
                    shakeOffset.animateTo(10f, tween(50))
                    shakeOffset.animateTo(-10f, tween(50))
                    shakeOffset.animateTo(10f, tween(50))
                    shakeOffset.animateTo(0f, tween(50))
                }

                delay(500)
                val matchedPairs = currentState.matchedPairs.toMutableSet()
                matchedPairs.addAll(indices)
                
                val newState = currentState.copy(
                    score = currentState.score + 10,
                    matchedPairs = matchedPairs,
                    flippedIndices = emptySet(),
                    shakingCards = emptySet(),
                    isComparing = false
                )
                saveGameState(newState)
            } else {
                launch {
                    delay(1000)
                    indices.forEach { index ->
                        scope.launch {
                            rotations[index].animateTo(0f, tween(500))
                        }
                    }
                    
                    val newState = currentState.copy(
                        flippedIndices = emptySet(),
                        isComparing = false,
                        score = (currentState.score - 2).coerceAtLeast(0)
                    )
                    saveGameState(newState)
                }
            }
        }
    }

    fun retryCurrentLevel() {
        val currentDifficulty = _gameState.value.selectedDifficulty
        currentDifficulty?.let { difficulty ->
            cards = (currentEmojis + currentEmojis).shuffled()
            val newState = GameState(
                selectedDifficulty = difficulty,
                remainingTime = difficulty.timeLimit,
                currentDialog = DialogType.None
            )
            saveGameState(newState)
            startTimer()
        }
    }

    fun resetGame() {
        saveGameState(GameState(currentDialog = DialogType.Difficulty))
        timerJob?.cancel()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_gameState.value.remainingTime > 0 && !_gameState.value.isGameOver) {
                delay(1000)
                val currentState = _gameState.value
                val newState = currentState.copy(
                    remainingTime = currentState.remainingTime - 1,
                    isGameOver = currentState.remainingTime <= 1
                )
                saveGameState(newState)
            }
        }
    }

    fun pauseGame() {
        savedTimeRemaining = _gameState.value.remainingTime
        wasGameRunning = timerJob?.isActive == true
        timerJob?.cancel()
    }

    fun resumeGame() {
        if (wasGameRunning && savedTimeRemaining > 0) {
            val currentState = _gameState.value
            saveGameState(currentState.copy(remainingTime = savedTimeRemaining))
            startTimer()
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (_gameState.value.score > 0) {
            saveScore()
        }
        timerJob?.cancel()
    }

    fun onGameCompleted() {
        timerJob?.cancel()
        if (_gameState.value.score > 0) {
            saveScore()
        }
    }
} 