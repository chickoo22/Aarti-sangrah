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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.app.viewmodel.AartiViewModel
import com.example.notifications.NotificationHelper
import com.example.ui.screens.*
import com.example.ui.theme.MantramayaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize local notification channels and sync reminders (100% on-device, zero backend)
        NotificationHelper.createNotificationChannels(this)
        NotificationHelper.syncAllRemindersFromPreferences(this)

        val targetAartiIdFromIntent = intent?.getIntExtra(NotificationHelper.EXTRA_TARGET_AARTI_ID, -1)?.takeIf { it > 0 }

        setContent {
            MantramayaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: AartiViewModel = viewModel()

                    LaunchedEffect(targetAartiIdFromIntent) {
                        if (targetAartiIdFromIntent != null) {
                            navController.navigate("reader/$targetAartiIdFromIntent")
                        }
                    }

                    NavHost(navController = navController, startDestination = "splash") {
                        composable("splash") {
                            SplashScreen(
                                viewModel = viewModel,
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                },
                                onNavigateToOnboarding = {
                                    navController.navigate("onboarding") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("onboarding") {
                            OnboardingScreen(
                                viewModel = viewModel,
                                onFinishOnboarding = {
                                    navController.navigate("home") {
                                        popUpTo("onboarding") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("home") {
                            HomeScreen(
                                viewModel = viewModel,
                                onAartiClick = { aartiId -> navController.navigate("reader/$aartiId") },
                                onNavigateToBhajans = { navController.navigate("bhajans") },
                                onNavigateToFavorites = { navController.navigate("favorites") },
                                onNavigateToReminders = { navController.navigate("reminders") },
                                onNavigateToSettings = { navController.navigate("settings") }
                            )
                        }
                        composable("bhajans") {
                            BhajansScreen(
                                viewModel = viewModel,
                                onBhajanClick = { aartiId -> navController.navigate("reader/$aartiId") },
                                onBack = { navController.popBackStack() }
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
                                onNavigateToOnboarding = { navController.navigate("onboarding") },
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
