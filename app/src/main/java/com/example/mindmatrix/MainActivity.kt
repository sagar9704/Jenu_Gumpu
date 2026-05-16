package com.example.mindmatrix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mindmatrix.ui.*
import com.example.mindmatrix.ui.screens.*
import com.example.mindmatrix.ui.theme.MindMatrixTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var currentLanguage by remember { mutableStateOf("en") }
            val strings = if (currentLanguage == "kn") KannadaStrings else EnglishStrings

            CompositionLocalProvider(LocalStrings provides strings) {
                MindMatrixTheme {
                    AppNavigation(onLanguageChanged = { currentLanguage = it })
                }
            }
        }
    }
}

@Composable
fun AppNavigation(onLanguageChanged: (String) -> Unit) {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = Screen.LanguageSelection.route) {
        composable(Screen.LanguageSelection.route) {
            LanguageSelectionScreen(onLanguageSelected = { lang ->
                onLanguageChanged(lang)
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.LanguageSelection.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToAddHarvest = { navController.navigate(Screen.AddHarvest.route) },
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToGrading = { navController.navigate(Screen.GradingGuide.route) },
                onNavigateToCalculator = { navController.navigate(Screen.ProfitCalculator.route) },
                onNavigateToSustainable = { navController.navigate(Screen.SustainableHarvest.route) },
                onNavigateToPriceMonitor = { navController.navigate(Screen.MarketPriceMonitor.route) },
                onNavigateToCharts = { navController.navigate(Screen.Charts.route) },
                onLanguageChanged = onLanguageChanged
            )
        }
        composable(Screen.AddHarvest.route) {
            AddHarvestScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.History.route) {
            HistoryScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.ProfitCalculator.route) {
            ProfitCalculatorScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.GradingGuide.route) {
            GradingGuideScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.SustainableHarvest.route) {
            SustainableHarvestScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.MarketPriceMonitor.route) {
            MarketPriceMonitorScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.Charts.route) {
            ChartsScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}