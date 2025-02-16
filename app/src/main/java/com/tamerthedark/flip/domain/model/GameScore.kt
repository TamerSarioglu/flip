package com.tamerthedark.flip.domain.model

data class GameScore(
    val id: Int = 0,
    val score: Int,
    val difficulty: String,
    val timestamp: Long = System.currentTimeMillis()
)
