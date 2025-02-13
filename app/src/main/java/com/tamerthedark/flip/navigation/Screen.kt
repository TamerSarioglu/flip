package com.tamerthedark.flip.navigation

sealed class Screen(val route: String) {
    object Game : Screen("game")
    object Scores : Screen("scores")
} 