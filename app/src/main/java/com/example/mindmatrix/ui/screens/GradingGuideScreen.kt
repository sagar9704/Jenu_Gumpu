package com.example.mindmatrix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindmatrix.ui.LocalStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradingGuideScreen(
    onNavigateBack: () -> Unit
) {
    val strings = LocalStrings.current
    var selectedGrade by remember { mutableStateOf("A") }

    val primaryColor = Color(0xFF8D4B00)
    val backgroundColor = Color(0xFFFAF9F4)
    val tertiaryColor = Color(0xFF904821)
    val onSurfaceVariant = Color(0xFF554336)
    val surfaceContainerLow = Color(0xFFF5F4EF)
    val outlineVariant = Color(0xFFDBC2B0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        strings.qualityGuide,
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White.copy(alpha = 0.9f),
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Hero Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    strings.standardGrading,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor,
                    lineHeight = 40.sp
                )
                Text(
                    strings.gradingSubtitle,
                    fontSize = 18.sp,
                    color = onSurfaceVariant,
                    lineHeight = 28.sp
                )
            }

            // Honey Color Guide Section
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Palette, contentDescription = null, tint = tertiaryColor)
                    Text(strings.honeyColorGuide, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = surfaceContainerLow),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, outlineVariant.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Gradient Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFFFDE68A),
                                            Color(0xFFF59E0B),
                                            Color(0xFF78350F)
                                        )
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                        )

                        // Color Swatches
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ColorSwatch(Color(0xFFFDE68A), strings.lightAmber, Modifier.weight(1f))
                            ColorSwatch(Color(0xFFF59E0B), strings.amber, Modifier.weight(1f))
                            ColorSwatch(Color(0xFF78350F), strings.darkAmber, Modifier.weight(1f))
                        }
                    }
                }
            }

            // Honey Color Analyzer Section
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = tertiaryColor)
                    Text(strings.colorAnalyzer, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                }

                val context = androidx.compose.ui.platform.LocalContext.current
                var analyzedGrade by remember { mutableStateOf<String?>(null) }
                var isAnalyzing by remember { mutableStateOf(false) }

                val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
                    androidx.activity.result.contract.ActivityResultContracts.GetContent()
                ) { uri: android.net.Uri? ->
                    uri?.let {
                        isAnalyzing = true
                        // Basic color analysis
                        val inputStream = context.contentResolver.openInputStream(it)
                        val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                        
                        if (bitmap != null) {
                            // Sample a few pixels from the center
                            val centerX = bitmap.width / 2
                            val centerY = bitmap.height / 2
                            val pixel = bitmap.getPixel(centerX, centerY)
                            
                            val red = android.graphics.Color.red(pixel)
                            val green = android.graphics.Color.green(pixel)
                            val blue = android.graphics.Color.blue(pixel)
                            
                            // Simple heuristic for honey grades
                            // Light honey (high RGB) -> Grade A
                            // Medium honey -> Grade B
                            // Dark honey -> Grade C
                            val brightness = (red + green + blue) / 3
                            analyzedGrade = when {
                                brightness > 180 -> "A"
                                brightness > 100 -> "B"
                                else -> "C"
                            }
                        }
                        isAnalyzing = false
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = surfaceContainerLow),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = strings.colorAnalyzerDesc,
                            textAlign = TextAlign.Center,
                            fontSize = 15.sp,
                            color = onSurfaceVariant
                        )

                        Button(
                            onClick = { launcher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(strings.uploadImage)
                        }

                        if (isAnalyzing) {
                            CircularProgressIndicator(color = primaryColor)
                        }

                        analyzedGrade?.let { grade ->
                            Surface(
                                color = Color(0xFFFFDCC3),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(strings.suggestedGrade, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = grade,
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.Black,
                                        color = primaryColor
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Moisture Test Section
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Opacity, contentDescription = null, tint = tertiaryColor)
                    Text(strings.moistureTest, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                }

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    MoistureStepCard(1, strings.spoonDropTitle, strings.spoonDropDesc, primaryColor)
                    MoistureStepCard(2, strings.thumbTestTitle, strings.thumbTestDesc, primaryColor)
                    MoistureStepCard(3, strings.waterGlassTitle, strings.waterGlassDesc, primaryColor)
                }
            }

            // Select Grade Section
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = tertiaryColor)
                    Text(strings.selectGrade, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    GradeButton(
                        grade = "A",
                        title = strings.gradeAPremium,
                        desc = strings.gradeADesc,
                        isSelected = selectedGrade == "A",
                        primaryColor = primaryColor,
                        onClick = { selectedGrade = "A" }
                    )
                    GradeButton(
                        grade = "B",
                        title = strings.gradeBStandard,
                        desc = strings.gradeBDesc,
                        isSelected = selectedGrade == "B",
                        primaryColor = primaryColor,
                        onClick = { selectedGrade = "B" }
                    )
                    GradeButton(
                        grade = "C",
                        title = strings.gradeCCommercial,
                        desc = strings.gradeCDesc,
                        isSelected = selectedGrade == "C",
                        primaryColor = primaryColor,
                        onClick = { selectedGrade = "C" }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ColorSwatch(color: Color, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(color, RoundedCornerShape(6.dp))
                .border(1.dp, Color(0xFFDBC2B0), RoundedCornerShape(6.dp))
        )
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
    }
}

@Composable
fun MoistureStepCard(step: Int, title: String, desc: String, primaryColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3E3DE).copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDBC2B0).copy(alpha = 0.2f))
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFFFDCC3), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(step.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = primaryColor)
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title.uppercase(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Text(desc, fontSize = 16.sp, lineHeight = 24.sp)
            }
        }
    }
}

@Composable
fun GradeButton(
    grade: String,
    title: String,
    desc: String,
    isSelected: Boolean,
    primaryColor: Color,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) primaryColor else Color(0xFFDBC2B0)
    val borderWidth = if (isSelected) 2.dp else 1.dp
    val backgroundColor = if (isSelected) Color(0xFFEFEEE9) else Color.White

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(borderWidth, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = if (isSelected) primaryColor else Color.Black)
                Text(desc, fontSize = 16.sp, color = Color(0xFF554336))
            }
            Icon(
                imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isSelected) primaryColor else Color(0xFF887364)
            )
        }
    }
}
