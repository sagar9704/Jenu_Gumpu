package com.example.mindmatrix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mindmatrix.ui.LocalStrings
import com.example.mindmatrix.ui.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToAddHarvest: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToGrading: () -> Unit,
    onNavigateToCalculator: () -> Unit,
    onNavigateToSustainable: () -> Unit,
    onNavigateToPriceMonitor: () -> Unit,
    onNavigateToCharts: () -> Unit,
    onLanguageChanged: (String) -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    var languageMenuExpanded by remember { mutableStateOf(false) }
    val stock = viewModel.stockState.value
    val floralStock = viewModel.floralStockState.value
    val isLoading = viewModel.isLoading.value
    val strings = LocalStrings.current

    LaunchedEffect(Unit) {
        viewModel.fetchStock()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.appName, color = Color(0xFF78350F), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFDFCF7)),
                actions = {
                    Box {
                        IconButton(onClick = { languageMenuExpanded = true }) {
                            Icon(Icons.Default.Language, contentDescription = "Change Language", tint = Color(0xFF78350F))
                        }
                        DropdownMenu(
                            expanded = languageMenuExpanded,
                            onDismissRequest = { languageMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("English") },
                                onClick = {
                                    onLanguageChanged("en")
                                    languageMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("ಕನ್ನಡ (Kannada)") },
                                onClick = {
                                    onLanguageChanged("kn")
                                    languageMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFDFCF7))
                .padding(padding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = strings.collectiveStock,
                    fontSize = 16.sp,
                    color = Color(0xFF14532D)
                )
            }

            // Collective Stock Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD97706)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(strings.totalCollectiveStock, color = Color.White, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (isLoading && stock == null) {
                            CircularProgressIndicator(color = Color.White)
                        } else {
                            Text(
                                text = "${stock?.totalQuantity ?: 0.0} ${stock?.unit ?: "kg"}",
                                color = Color.White,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Text(strings.fromEntries.format(stock?.entryCount ?: 0), color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }

            // Floral Source Breakdown
            if (floralStock.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.floralSourceBreakdown,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF78350F)
                            )
                            TextButton(onClick = onNavigateToCharts) {
                                Text(strings.viewDetailedCharts, color = Color(0xFF14532D))
                            }
                        }
                        
                        floralStock.take(3).forEach { item ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp),
                                shadowElevation = 1.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(item.floralSource, fontWeight = FontWeight.Medium)
                                    Text("${item.totalQuantity} kg", fontWeight = FontWeight.Bold, color = Color(0xFF14532D))
                                }
                            }
                        }
                    }
                }
            }

            // Quick Actions Grid
            item {
                Column(modifier = Modifier.padding(bottom = 20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ActionCard(strings.addHarvest, Icons.Default.Add, Color(0xFF14532D), Modifier.weight(1f), onNavigateToAddHarvest)
                        ActionCard(strings.history, Icons.Default.List, Color(0xFF78350F), Modifier.weight(1f), onNavigateToHistory)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ActionCard(strings.gradingGuide, Icons.Default.Info, Color(0xFF78350F), Modifier.weight(1f), onNavigateToGrading)
                        ActionCard(strings.calculator, Icons.Default.Calculate, Color(0xFF14532D), Modifier.weight(1f), onNavigateToCalculator)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ActionCard(strings.priceMonitor, Icons.Default.TrendingUp, Color(0xFF8D4B00), Modifier.weight(1f), onNavigateToPriceMonitor)
                        ActionCard(strings.sustainableHarvest, Icons.Default.Favorite, Color(0xFF14532D), Modifier.weight(1f), onNavigateToSustainable)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                         ActionCard(strings.charts, Icons.Default.PieChart, Color(0xFFD97706), Modifier.fillMaxWidth(), onNavigateToCharts)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionCard(title: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title, 
                fontWeight = FontWeight.SemiBold, 
                color = Color(0xFF78350F),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 4.dp),
                lineHeight = 16.sp
            )
        }
    }
}
