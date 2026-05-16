package com.example.mindmatrix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mindmatrix.ui.LocalStrings
import com.example.mindmatrix.ui.viewmodel.MarketPriceMonitorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketPriceMonitorScreen(
    onNavigateBack: () -> Unit,
    viewModel: MarketPriceMonitorViewModel = viewModel()
) {
    val prices = viewModel.prices.value
    val isLoading = viewModel.isLoading.value
    val strings = LocalStrings.current

    val primaryColor = Color(0xFF8D4B00)
    val secondaryColor = Color(0xFF2E6A41)
    val backgroundColor = Color(0xFFFAF9F4)

    LaunchedEffect(Unit) {
        viewModel.fetchPrices()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.marketPrices) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.fetchPrices() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = primaryColor,
                    navigationIconContentColor = primaryColor,
                    actionIconContentColor = primaryColor
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
        ) {
            // Mesh Background Effect
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFFFFB77D).copy(alpha = 0.1f), Color.Transparent),
                            center = androidx.compose.ui.geometry.Offset(0f, 0f),
                            radius = 800f
                        )
                    )
            )

            if (isLoading && prices.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primaryColor)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Column(modifier = Modifier.padding(bottom = 8.dp)) {
                            Text(
                                text = strings.marketPrices,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                            Text(
                                text = "Real-time reference prices from regional markets.",
                                fontSize = 16.sp,
                                color = Color(0xFF554336).copy(alpha = 0.7f)
                            )
                        }
                    }

                    if (prices.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No price data available.", color = Color.Gray)
                            }
                        }
                    } else {
                        items(prices) { marketPrice ->
                            MarketPriceCard(
                                source = marketPrice.source,
                                price = marketPrice.price,
                                unit = marketPrice.unit,
                                primaryColor = primaryColor,
                                secondaryColor = secondaryColor
                            )
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun MarketPriceCard(
    source: String,
    price: Double,
    unit: String,
    primaryColor: Color,
    secondaryColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDBC2B0).copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(secondaryColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = secondaryColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = source,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1B1C19)
                )
                Text(
                    text = "Reference Market",
                    fontSize = 14.sp,
                    color = Color(0xFF554336).copy(alpha = 0.6f)
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${String.format("%.2f", price)}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = primaryColor
                )
                Text(
                    text = "/ $unit",
                    fontSize = 12.sp,
                    color = Color(0xFF554336).copy(alpha = 0.6f)
                )
            }
        }
    }
}
