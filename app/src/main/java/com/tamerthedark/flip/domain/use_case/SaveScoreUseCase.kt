package com.tamerthedark.flip.domain.use_case

import com.tamerthedark.flip.domain.model.GameScore
import com.tamerthedark.flip.domain.repository.GameRepository
import javax.inject.Inject

class SaveScoreUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(score: GameScore) {
        repository.insertScore(score)
    }
} 