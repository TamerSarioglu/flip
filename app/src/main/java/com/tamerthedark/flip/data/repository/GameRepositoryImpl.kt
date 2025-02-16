package com.tamerthedark.flip.data.repository

import com.tamerthedark.flip.data.local.GameDao
import com.tamerthedark.flip.data.mapper.toDomain
import com.tamerthedark.flip.data.mapper.toEntity
import com.tamerthedark.flip.domain.model.GameScore
import com.tamerthedark.flip.domain.model.GameState
import com.tamerthedark.flip.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepositoryImpl @Inject constructor(
    private val dao: GameDao
) : GameRepository {
    private val gameState = MutableStateFlow(GameState())

    override suspend fun insertScore(score: GameScore) {
        dao.insertScore(score.toEntity())
    }

    override fun getAllScores(): Flow<List<GameScore>> {
        return dao.getAllScores().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getScoresByDifficulty(difficulty: String): Flow<List<GameScore>> {
        return dao.getScoresByDifficulty(difficulty).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveGameState(gameState: GameState) {
        this.gameState.value = gameState
    }

    override fun getGameState(): Flow<GameState> = gameState
} 