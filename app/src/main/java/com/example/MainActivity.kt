package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.app.viewmodel.AartiViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MantramayaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MantramayaTheme {
                val navController = rememberNavController()
                val viewModel: AartiViewModel = viewModel()

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            viewModel = viewModel,
                            onAartiClick = { aartiId -> navController.navigate("reader/$aartiId") },
                            onNavigateToFavorites = { navController.navigate("favorites") },
                            onNavigateToReminders = { navController.navigate("reminders") },
                            onNavigateToSettings = { navController.navigate("settings") }
                        )
                    }
                    composable(
                        route = "reader/{aartiId}",
                        arguments = listOf(navArgument("aartiId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val aartiId = backStackEntry.arguments?.getInt("aartiId") ?: 0
                        ReaderScreen(
                            aartiId = aartiId,
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("favorites") {
                        FavoritesScreen(
                            viewModel = viewModel,
                            onAartiClick = { aartiId -> navController.navigate("reader/$aartiId") },
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("reminders") {
                        RemindersScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("settings") {
                        SettingsScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
