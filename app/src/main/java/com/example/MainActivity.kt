package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.*
import com.example.ui.theme.DivineAartiTheme
import com.example.viewmodel.AartiViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        com.google.android.gms.ads.MobileAds.initialize(this) {}
        setContent {
            val viewModel: AartiViewModel = viewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            DivineAartiTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "home",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("home") {
                        HomeScreen(
                            viewModel = viewModel,
                            onAartiClick = { aartiId -> navController.navigate("detail/$aartiId") },
                            onNavigateToFavorites = { navController.navigate("favorites") },
                            onNavigateToReminders = { navController.navigate("reminders") },
                            onNavigateToSettings = { navController.navigate("settings") }
                        )
                    }
                    composable(
                        route = "detail/{aartiId}",
                        arguments = listOf(navArgument("aartiId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val aartiId = backStackEntry.arguments?.getInt("aartiId") ?: 1
                        AartiDetailScreen(
                            aartiId = aartiId,
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("favorites") {
                        FavoritesScreen(
                            viewModel = viewModel,
                            onAartiClick = { aartiId -> navController.navigate("detail/$aartiId") },
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
