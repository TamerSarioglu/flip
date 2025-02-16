package com.tamerthedark.flip.domain.use_case

data class GameUseCases(
    val getGameState: GetGameStateUseCase,
    val saveGameState: SaveGameStateUseCase,
    val saveScore: SaveScoreUseCase,
    val getScores: GetScoresUseCase
) 