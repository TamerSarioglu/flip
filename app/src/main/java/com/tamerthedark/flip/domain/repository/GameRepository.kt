package com.tamerthedark.flip.domain.repository

import com.tamerthedark.flip.domain.model.GameScore
import com.tamerthedark.flip.domain.model.GameState
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    suspend fun insertScore(score: GameScore)
    fun getAllScores(): Flow<List<GameScore>>
    fun getScoresByDifficulty(difficulty: String): Flow<List<GameScore>>
    suspend fun saveGameState(gameState: GameState)
    fun getGameState(): Flow<GameState>
} 