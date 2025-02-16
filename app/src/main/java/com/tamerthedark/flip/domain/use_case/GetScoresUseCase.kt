package com.tamerthedark.flip.domain.use_case

import com.tamerthedark.flip.domain.model.GameScore
import com.tamerthedark.flip.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetScoresUseCase @Inject constructor(
    private val repository: GameRepository
) {
    operator fun invoke(difficulty: String? = null): Flow<List<GameScore>> {
        return if (difficulty != null) {
            repository.getScoresByDifficulty(difficulty)
        } else {
            repository.getAllScores()
        }
    }
} 