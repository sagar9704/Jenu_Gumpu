package com.example.mindmatrix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.MyLocation
import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mindmatrix.ui.LocalStrings
import com.example.mindmatrix.ui.viewmodel.AddHarvestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHarvestScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddHarvestViewModel = viewModel()
) {
    var location by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var floralSource by remember { mutableStateOf("") }
    var grade by remember { mutableStateOf("A") }
    var hiveId by remember { mutableStateOf("") }
    var batchId by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var floralExpanded by remember { mutableStateOf(false) }
    var isLocating by remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, trigger location fetch
        }
    }

    val strings = LocalStrings.current
    val floralOptions = remember(strings) {
        listOf(
            strings.floralCoffee,
            strings.floralWildflower,
            strings.floralForest,
            strings.floralMultifloral,
            strings.floralAcacia
        )
    }

    val isSubmitting = viewModel.isSubmitting.value
    val isSuccess = viewModel.isSuccess.value
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    if (isSuccess) {
        LaunchedEffect(Unit) {
            onNavigateBack()
            viewModel.resetSuccess()
        }
    }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.addHarvest) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFD97706),
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = dateFormatter.format(Date(date)),
                onValueChange = { },
                label = { Text(strings.harvestDate) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    }
                }
            )

            if (showDatePicker) {
                val datePickerState = rememberDatePickerState(initialSelectedDateMillis = date)
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            date = datePickerState.selectedDateMillis ?: date
                            showDatePicker = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text(strings.location) },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (isLocating) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        IconButton(onClick = {
                            permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                            isLocating = true
                            try {
                                locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                                    .addOnSuccessListener { loc: android.location.Location? ->
                                        if (loc != null) {
                                            scope.launch {
                                                try {
                                                    val addressText = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                                                        val geocoder = Geocoder(context, Locale.getDefault())
                                                        val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                                                        if (!addresses.isNullOrEmpty()) {
                                                            val addr = addresses[0]
                                                            val fullAddress = addr.getAddressLine(0)
                                                            val coords = String.format(Locale.US, "(%.5f, %.5f)", loc.latitude, loc.longitude)
                                                            
                                                            if (fullAddress != null) {
                                                                "$fullAddress $coords"
                                                            } else {
                                                                val fallback = listOfNotNull(
                                                                    addr.subLocality,
                                                                    addr.locality,
                                                                    addr.subAdminArea
                                                                ).joinToString(", ")
                                                                if (fallback.isNotEmpty()) "$fallback $coords" else coords
                                                            }
                                                        } else {
                                                            String.format(Locale.US, "(%.5f, %.5f)", loc.latitude, loc.longitude)
                                                        }
                                                    }
                                                    location = addressText
                                                } catch (e: Exception) {
                                                    location = String.format(Locale.US, "(%.5f, %.5f)", loc.latitude, loc.longitude)
                                                } finally {
                                                    isLocating = false
                                                }
                                            }
                                        } else {
                                            isLocating = false
                                        }
                                    }
                                    .addOnFailureListener { isLocating = false }
                            } catch (e: SecurityException) {
                                isLocating = false
                            }
                        }) {
                            Icon(Icons.Default.MyLocation, contentDescription = "Get Current Location", tint = Color(0xFFD97706))
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = Color(0xFF78350F),
                    unfocusedLabelColor = Color(0xFF4B5563),
                    focusedBorderColor = Color(0xFFD97706),
                    unfocusedBorderColor = Color(0xFFD1D5DB)
                )
            )

            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text(strings.quantity) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = Color(0xFF78350F),
                    unfocusedLabelColor = Color(0xFF4B5563)
                )
            )

            ExposedDropdownMenuBox(
                expanded = floralExpanded,
                onExpandedChange = { floralExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = floralSource,
                    onValueChange = { floralSource = it },
                    label = { Text(strings.floralSource) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = floralExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                
                ExposedDropdownMenu(
                    expanded = floralExpanded,
                    onDismissRequest = { floralExpanded = false }
                ) {
                    floralOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                floralSource = option
                                floralExpanded = false
                            }
                        )
                    }
                }
            }


            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = hiveId,
                    onValueChange = { hiveId = it },
                    label = { Text(strings.hiveId) },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = batchId,
                    onValueChange = { batchId = it },
                    label = { Text(strings.batchId) },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text(strings.notes) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = Color(0xFF78350F),
                    unfocusedLabelColor = Color(0xFF4B5563)
                )
            )

            Text(strings.grade, fontWeight = FontWeight.SemiBold, color = Color(0xFF78350F))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                GradeIcon("A", Icons.Default.Star, Color(0xFF14532D), grade == "A") { grade = "A" }
                GradeIcon("B", Icons.Default.CheckCircle, Color(0xFFD97706), grade == "B") { grade = "B" }
                GradeIcon("C", Icons.Default.Info, Color(0xFF78350F), grade == "C") { grade = "C" }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { 
                    val q = quantity.toDoubleOrNull() ?: 0.0
                    viewModel.submitHarvest(
                        location, 
                        q, 
                        floralSource, 
                        grade, 
                        date, 
                        hiveId.ifEmpty { null }, 
                        batchId.ifEmpty { null }, 
                        notes.ifEmpty { null }
                    ) 
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14532D)),
                enabled = !isSubmitting && location.isNotEmpty() && quantity.isNotEmpty()
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(strings.saveHarvest, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun GradeIcon(label: String, icon: ImageVector, color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(56.dp)
                .background(
                    if (isSelected) color.copy(alpha = 0.2f) else Color.Transparent,
                    RoundedCornerShape(12.dp)
                )
        ) {
            Icon(icon, contentDescription = label, tint = if (isSelected) color else Color(0xFF6B7280), modifier = Modifier.size(32.dp))
        }
        Text(label, fontSize = 12.sp, color = if (isSelected) color else Color(0xFF6B7280), fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
    }
}
