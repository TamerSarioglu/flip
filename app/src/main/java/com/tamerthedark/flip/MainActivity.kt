package com.tamerthedark.flip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tamerthedark.flip.presentation.navigation.Screen
import com.tamerthedark.flip.presentation.screens.gamescreen.GameScreen
import com.tamerthedark.flip.presentation.screens.savedscorescreen.ScoresScreen
import com.tamerthedark.flip.ui.theme.FlipTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlipTheme {
                val navController = rememberNavController()
                val items = listOf(
                    NavigationItem(
                        route = Screen.Game.route,
                        icon = Icons.Default.Create,
                        label = "Game"
                    ),
                    NavigationItem(
                        route = Screen.Scores.route,
                        icon = Icons.Default.Create,
                        label = "Scores"
                    )
                )

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination

                            items.forEach { item ->
                                NavigationBarItem(
                                    icon = { item.icon },
                                    label = { Text(item.label) },
                                    selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                                    onClick = {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Game.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Game.route) {
                            GameScreen()
                        }
                        composable(Screen.Scores.route) { ScoresScreen() }
                    }
                }
            }
        }
    }
}

data class NavigationItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)