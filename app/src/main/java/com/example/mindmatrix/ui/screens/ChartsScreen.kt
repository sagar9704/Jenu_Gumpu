package com.example.mindmatrix.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mindmatrix.data.local.entity.FloralSourceStock
import com.example.mindmatrix.ui.LocalStrings
import com.example.mindmatrix.ui.viewmodel.DashboardViewModel
import com.example.mindmatrix.ui.viewmodel.Timeframe
import com.example.mindmatrix.ui.viewmodel.TimeSeriesData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartsScreen(
    onNavigateBack: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val floralStock = viewModel.floralStockState.value
    val timeSeriesData = viewModel.timeSeriesData.value
    val selectedTimeframe = viewModel.selectedTimeframe.value
    val isLoading = viewModel.isLoading.value
    val strings = LocalStrings.current

    val primaryColor = Color(0xFF8D4B00)
    val backgroundColor = Color(0xFFFAF9F4)

    LaunchedEffect(Unit) {
        viewModel.fetchStock()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.charts, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = primaryColor,
                    navigationIconContentColor = primaryColor
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
            if (isLoading && floralStock.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primaryColor)
                }
            } else if (floralStock.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No harvest data available for charts.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        Text(
                            text = "Harvest Over Time", // Using string literal as it's a new feature
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    }

                    item {
                        TimeframeSelector(
                            selected = selectedTimeframe,
                            onSelected = { viewModel.setTimeframe(it) }
                        )
                    }

                    item {
                        TimeComparisonChart(timeSeriesData)
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = strings.floralSourceBreakdown,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    }

                    item {
                        HorizontalDivider(color = Color(0xFFDBC2B0).copy(alpha = 0.3f))
                    }

                    items(floralStock) { item ->
                        FloralStockItem(item, strings.totalKgs)
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TimeframeSelector(selected: Timeframe, onSelected: (Timeframe) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Timeframe.values().forEach { timeframe ->
            val isSelected = selected == timeframe
            TextButton(
                onClick = { onSelected(timeframe) },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = if (isSelected) Color(0xFFD97706) else Color.Transparent,
                    contentColor = if (isSelected) Color.White else Color(0xFF8D4B00)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = timeframe.name.lowercase().replaceFirstChar { it.uppercase() },
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun TimeComparisonChart(data: List<TimeSeriesData>) {
    if (data.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth().height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No data available for this timeframe.", color = Color.Gray)
        }
        return
    }

    val maxQuantity = data.maxOfOrNull { it.totalQuantity }?.coerceAtLeast(1.0) ?: 1.0
    val barColor = Brush.verticalGradient(
        colors = listOf(Color(0xFFD97706), Color(0xFFB45309))
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val barWidth = width / (data.size * 2f).coerceAtLeast(1f)
                val spacing = width / (data.size * 2f).coerceAtLeast(1f)
                
                data.forEachIndexed { index, item ->
                    val barHeight = (item.totalQuantity / maxQuantity) * (height - 60f)
                    val x = spacing + index * (barWidth + spacing)
                    val y = height - barHeight.toFloat() - 40f
                    
                    // Draw Bar
                    drawRoundRect(
                        brush = barColor,
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight.toFloat()),
                        cornerRadius = CornerRadius(8f, 8f)
                    )
                    
                    // Draw Label (Abbreviated if too long)
                    drawIntoCanvas { canvas ->
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.GRAY
                            textSize = 28f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                        val label = if (item.label.length > 8) item.label.take(6) + ".." else item.label
                        canvas.nativeCanvas.drawText(
                            label,
                            x + barWidth / 2,
                            height - 10f,
                            paint
                        )
                        
                        val qtyPaint = android.graphics.Paint().apply {
                            color = 0xFF78350F.toInt()
                            textSize = 26f
                            textAlign = android.graphics.Paint.Align.CENTER
                            typeface = android.graphics.Typeface.DEFAULT_BOLD
                        }
                        canvas.nativeCanvas.drawText(
                            "${item.totalQuantity.toInt()}kg",
                            x + barWidth / 2,
                            y - 15f,
                            qtyPaint
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FloralStockItem(item: FloralSourceStock, labelKgs: String) {
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
            Column {
                Text(item.floralSource, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1B1C19))
                Text(labelKgs, fontSize = 12.sp, color = Color.Gray)
            }
            Text(
                text = "${item.totalQuantity} kg",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF14532D)
            )
        }
    }
}
