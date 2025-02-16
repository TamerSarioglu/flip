package com.tamerthedark.flip.domain.use_case

import com.tamerthedark.flip.domain.model.GameState
import com.tamerthedark.flip.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGameStateUseCase @Inject constructor(
    private val repository: GameRepository
) {
    operator fun invoke(): Flow<GameState> {
        return repository.getGameState()
    }
} 