package com.example.mindmatrix.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LanguageSelectionScreen(
    onLanguageSelected: (String) -> Unit
) {
    val primaryColor = Color(0xFF8D4B00)
    val secondaryColor = Color(0xFF2E6A41)
    val tertiaryColor = Color(0xFF904821)
    val backgroundColor = Color(0xFFFAF9F4)
    val onSurfaceVariant = Color(0xFF554336)
    val outlineVariant = Color(0xFFDBC2B0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Mesh Background Effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFFFFB77D).copy(alpha = 0.15f), Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(0f, 0f),
                        radius = 1000f
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF2E6A41).copy(alpha = 0.1f), Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(1000f, 2000f),
                        radius = 1000f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Section
            Box(
                modifier = Modifier
                    .padding(bottom = 40.dp)
                    .size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                // Glow effect
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .background(Color(0xFFFFDCC3).copy(alpha = 0.2f), CircleShape)
                        .blur(32.dp)
                )
                // Actual Logo Circle
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color(0xFFEFEEE9), CircleShape)
                        .border(1.dp, outlineVariant.copy(alpha = 0.3f), CircleShape)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.example.mindmatrix.R.drawable.ic_logo),
                        contentDescription = "Jenu-Gumpu Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
            }

            // Title Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 40.dp)
            ) {
                Text(
                    text = "Jenu-Gumpu",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Select Your Language\nನಿಮ್ಮ ಭಾಷೆಯನ್ನು ಆಯ್ಕೆ ಮಾಡಿ",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Language Options
            Column(
                modifier = Modifier.widthIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LanguageSelectionCard(
                    title = "English",
                    subtitle = "Standard English",
                    icon = Icons.Default.Language,
                    iconBgColor = Color(0xFFFFDCC3),
                    iconColor = primaryColor,
                    onClick = { onLanguageSelected("en") }
                )
                LanguageSelectionCard(
                    title = "ಕನ್ನಡ (Kannada)",
                    subtitle = "ಕರ್ನಾಟಕ ಪ್ರಾದೇಶಿಕ",
                    icon = Icons.Default.Translate,
                    iconBgColor = Color(0xFFB1F2BE),
                    iconColor = secondaryColor,
                    onClick = { onLanguageSelected("kn") }
                )
            }

            // Footnote
            Text(
                text = "Empowering tribal honey hunters through fair trade and digital dignity.",
                fontSize = 16.sp,
                fontStyle = FontStyle.Italic,
                color = onSurfaceVariant.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 40.dp)
                    .width(280.dp)
            )
        }

        // Bottom Gradient Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(primaryColor, tertiaryColor, secondaryColor)
                    ),
                    alpha = 0.8f
                )
        )
    }
}

@Composable
fun LanguageSelectionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBgColor: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clickable { onClick() },
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFDBC2B0).copy(alpha = 0.5f)),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconBgColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(28.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, color = Color(0xFF1B1C19), fontSize = 18.sp)
                Text(text = subtitle, color = Color(0xFF554336).copy(alpha = 0.7f), fontSize = 14.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF887364))
        }
    }
}
