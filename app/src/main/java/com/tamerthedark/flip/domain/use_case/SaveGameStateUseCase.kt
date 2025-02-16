package com.tamerthedark.flip.domain.use_case

import com.tamerthedark.flip.domain.model.GameState
import com.tamerthedark.flip.domain.repository.GameRepository
import javax.inject.Inject

class SaveGameStateUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(gameState: GameState) {
        repository.saveGameState(gameState)
    }
} 