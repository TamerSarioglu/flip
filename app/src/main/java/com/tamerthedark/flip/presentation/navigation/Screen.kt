package com.tamerthedark.flip.presentation.navigation

sealed class Screen(val route: String) {
    data object Game : Screen("game")
    data object Scores : Screen("scores")
} 