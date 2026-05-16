package com.example.mindmatrix.ui

sealed class Screen(val route: String) {
    object LanguageSelection : Screen("language_selection")
    object Dashboard : Screen("dashboard")
    object AddHarvest : Screen("add_harvest")
    object History : Screen("history")
    object GradingGuide : Screen("grading_guide")
    object ProfitCalculator : Screen("profit_calculator")
    object SustainableHarvest : Screen("sustainable_harvest")
    object MarketPriceMonitor : Screen("market_price_monitor")
    object Charts : Screen("charts")
}
