package com.example.mindmatrix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mindmatrix.data.local.entity.HarvestLogEntity
import com.example.mindmatrix.ui.LocalStrings
import com.example.mindmatrix.ui.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: HistoryViewModel = viewModel()
) {
    val logs = viewModel.logsState.value
    val isLoading = viewModel.isLoading.value
    val strings = LocalStrings.current

    val primaryColor = Color(0xFF8D4B00)
    val secondaryColor = Color(0xFF2E6A41)
    val backgroundColor = Color(0xFFFAF9F4)
    val tertiaryColor = Color(0xFF904821)

    LaunchedEffect(Unit) {
        viewModel.fetchHistory()
    }

    val totalHarvest = logs.sumOf { it.quantity }
    val seasonalGoal = 500.0 // Mocked seasonal goal
    val progress = (totalHarvest / seasonalGoal).coerceIn(0.0, 1.0)

    val tertColor = tertiaryColor
    val context = androidx.compose.ui.platform.LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        strings.history,
                        fontWeight = FontWeight.Black,
                        color = primaryColor,
                        letterSpacing = (-0.5).sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF554336))
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.exportToCsv(context) }) {
                        Icon(Icons.Default.FileDownload, contentDescription = strings.exportToCsv, tint = primaryColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
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

            if (isLoading && logs.isEmpty()) {
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
                        HeaderSection(strings.history, strings.trackSeasonalLogs, primaryColor)
                    }

                    item {
                        SeasonalSummaryCard(
                            totalHarvest = totalHarvest,
                            progress = progress.toFloat(),
                            strings = strings,
                            primaryColor = primaryColor,
                            secondaryColor = secondaryColor,
                            tertiaryColor = tertiaryColor
                        )
                    }

                    if (logs.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No harvest records found.", color = Color.Gray)
                            }
                        }
                    } else {
                        items(logs.reversed()) { log ->
                            RedesignedHarvestItem(
                                log = log,
                                primaryColor = primaryColor,
                                secondaryColor = secondaryColor,
                                tertiaryColor = tertiaryColor,
                                onDelete = { viewModel.deleteLog(log) }
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
fun HeaderSection(title: String, subtitle: String, color: Color) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(
            text = title,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = color,
            letterSpacing = (-1).sp
        )
        Text(
            text = subtitle,
            fontSize = 16.sp,
            color = Color(0xFF554336).copy(alpha = 0.7f),
            lineHeight = 22.sp
        )
    }
}

@Composable
fun SeasonalSummaryCard(
    totalHarvest: Double,
    progress: Float,
    strings: com.example.mindmatrix.ui.AppStrings,
    primaryColor: Color,
    secondaryColor: Color,
    tertiaryColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = tertiaryColor,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(tertiaryColor, Color(0xFF78350F))
                    )
                )
                .padding(24.dp)
        ) {
            Text(
                text = strings.seasonalYield.uppercase(),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = String.format("%.0f", totalHarvest),
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "kg ${strings.totalHarvest}",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color(0xFFFCD34D),
                trackColor = Color.White.copy(alpha = 0.2f),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = strings.seasonalGoalAchieved.format((progress * 100).toInt()),
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun RedesignedHarvestItem(
    log: HarvestLogEntity,
    primaryColor: Color,
    secondaryColor: Color,
    tertiaryColor: Color,
    onDelete: () -> Unit
) {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val dateString = sdf.format(Date(log.date))

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
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
                    .size(56.dp)
                    .background(primaryColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getFloralIcon(log.floralSource),
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.floralSource,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1B1C19)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = log.location,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
                
                // Added Hive and Batch IDs
                if (!log.hiveId.isNullOrEmpty() || !log.batchId.isNullOrEmpty()) {
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!log.hiveId.isNullOrEmpty()) {
                            Badge(containerColor = Color(0xFFF3F4F6)) {
                                Text("HIVE: ${log.hiveId}", color = Color(0xFF374151), fontSize = 10.sp)
                            }
                        }
                        if (!log.batchId.isNullOrEmpty()) {
                            Badge(containerColor = Color(0xFFF3F4F6)) {
                                Text("BATCH: ${log.batchId}", color = Color(0xFF374151), fontSize = 10.sp)
                            }
                        }
                    }
                }

                Text(
                    text = dateString,
                    fontSize = 12.sp,
                    color = primaryColor.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${String.format("%.0f", log.quantity)} kg",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = secondaryColor
                )
                
                Surface(
                    color = primaryColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "GRADE ${log.grade}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = primaryColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
                
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp).padding(top = 8.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

fun getFloralIcon(source: String): ImageVector {
    return when {
        source.contains("Forest", ignoreCase = true) -> Icons.Default.Park
        source.contains("Wildflower", ignoreCase = true) -> Icons.Default.FilterVintage
        source.contains("Coffee", ignoreCase = true) -> Icons.Default.Coffee
        source.contains("Acacia", ignoreCase = true) -> Icons.Default.Eco
        else -> Icons.Default.Grain
    }
}
