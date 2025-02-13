package com.tamerthedark.flip

data class GameState(
    val score: Int = 0,
    val flippedIndices: Set<Int> = emptySet(),
    val matchedPairs: Set<Int> = emptySet(),
    val canClick: Boolean = true,
    val shakingCards: Set<Int> = emptySet(),
    val isAnimating: Boolean = false,
    val isComparing: Boolean = false,
    val selectedDifficulty: DifficultyLevel? = null,
    val remainingTime: Int = 0,
    val isGameOver: Boolean = false
)