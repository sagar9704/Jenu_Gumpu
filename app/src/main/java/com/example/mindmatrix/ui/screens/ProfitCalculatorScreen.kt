package com.example.mindmatrix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mindmatrix.ui.LocalStrings
import com.example.mindmatrix.ui.viewmodel.ProfitCalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfitCalculatorScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfitCalculatorViewModel = viewModel()
) {
    var quantity by remember { mutableStateOf("") }
    var pricePerKg by remember { mutableStateOf("") }
    var costs by remember { mutableStateOf("") }
    var profit by remember { mutableStateOf(0.0) }
    val strings = LocalStrings.current

    val marketPrices = viewModel.prices.value

    LaunchedEffect(Unit) {
        viewModel.fetchPrices()
    }

    LaunchedEffect(quantity, pricePerKg, costs) {
        val q = quantity.toDoubleOrNull() ?: 0.0
        val p = pricePerKg.toDoubleOrNull() ?: 0.0
        val c = costs.toDoubleOrNull() ?: 0.0
        profit = viewModel.calculateProfit(q, p, c)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.calculator) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF14532D),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFDFCF7))
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(strings.estimateEarnings, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF78350F))

            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text(strings.quantity) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = Color(0xFF14532D),
                    unfocusedLabelColor = Color(0xFF4B5563)
                )
            )

            OutlinedTextField(
                value = pricePerKg,
                onValueChange = { pricePerKg = it },
                label = { Text(strings.pricePerKg) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = Color(0xFF14532D),
                    unfocusedLabelColor = Color(0xFF4B5563)
                )
            )

            OutlinedTextField(
                value = costs,
                onValueChange = { costs = it },
                label = { Text(strings.processingCosts) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = Color(0xFF14532D),
                    unfocusedLabelColor = Color(0xFF4B5563)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD97706).copy(alpha = 0.1f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(strings.estimatedProfit, color = Color(0xFF78350F))
                    Text(
                        text = "₹ ${String.format("%.2f", profit)}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (profit >= 0) Color(0xFF14532D) else Color.Red
                    )
                }
            }

            if (marketPrices.isNotEmpty()) {
                Text(strings.marketPrices, fontWeight = FontWeight.Bold, color = Color(0xFF78350F))
                marketPrices.forEach { mp ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(mp.source)
                        Text("₹ ${mp.price} / ${mp.unit}", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
