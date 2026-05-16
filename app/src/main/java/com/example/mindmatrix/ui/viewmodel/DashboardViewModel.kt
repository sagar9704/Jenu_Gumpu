package com.example.mindmatrix.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindmatrix.MindMatrixApp
import com.example.mindmatrix.data.local.entity.FloralSourceStock
import com.example.mindmatrix.data.local.entity.HarvestLogEntity
import com.example.mindmatrix.network.StockResponse
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

enum class Timeframe { DAILY, WEEKLY, MONTHLY, YEARLY }

data class TimeSeriesData(val label: String, val totalQuantity: Double)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val _stockState = mutableStateOf<StockResponse?>(null)
    val stockState: State<StockResponse?> = _stockState

    private val _floralStockState = mutableStateOf<List<FloralSourceStock>>(emptyList())
    val floralStockState: State<List<FloralSourceStock>> = _floralStockState

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _allLogs = mutableStateOf<List<HarvestLogEntity>>(emptyList())

    private val _selectedTimeframe = mutableStateOf(Timeframe.DAILY)
    val selectedTimeframe: State<Timeframe> = _selectedTimeframe

    private val _timeSeriesData = mutableStateOf<List<TimeSeriesData>>(emptyList())
    val timeSeriesData: State<List<TimeSeriesData>> = _timeSeriesData

    private val app = application as MindMatrixApp
    private val database = app.database
    private val sessionManager = app.sessionManager

    fun setTimeframe(timeframe: Timeframe) {
        _selectedTimeframe.value = timeframe
        computeTimeSeries()
    }

    private fun computeTimeSeries() {
        val logs = _allLogs.value
        if (logs.isEmpty()) {
            _timeSeriesData.value = emptyList()
            return
        }

        val grouped = when (_selectedTimeframe.value) {
            Timeframe.DAILY -> {
                val format = SimpleDateFormat("MMM dd", Locale.getDefault())
                logs.groupBy { format.format(it.date) }
            }
            Timeframe.WEEKLY -> {
                val format = SimpleDateFormat("MMM 'W'W", Locale.getDefault())
                logs.groupBy { format.format(it.date) }
            }
            Timeframe.MONTHLY -> {
                val format = SimpleDateFormat("MMM yy", Locale.getDefault())
                logs.groupBy { format.format(it.date) }
            }
            Timeframe.YEARLY -> {
                val format = SimpleDateFormat("yyyy", Locale.getDefault())
                logs.groupBy { format.format(it.date) }
            }
        }

        val sortedSeries = grouped.map { (label, logsInGroup) ->
            val minDate = logsInGroup.minOf { it.date }
            Triple(label, logsInGroup.sumOf { it.quantity }, minDate)
        }.sortedBy { it.third }.map { TimeSeriesData(it.first, it.second) }

        _timeSeriesData.value = sortedSeries.takeLast(7)
    }

    fun fetchStock() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val userId = sessionManager.getUserId()
                if (userId != -1) {
                    val totalQuantity = database.harvestLogDao().getTotalQuantityForUser(userId) ?: 0.0
                    val entryCount = database.harvestLogDao().getEntryCountForUser(userId)
                    _stockState.value = StockResponse(totalQuantity, "kg", entryCount)
                    
                    val floralStock = database.harvestLogDao().getQuantityByFloralSource(userId)
                    _floralStockState.value = floralStock

                    val logs = database.harvestLogDao().getLogsForUser(userId)
                    _allLogs.value = logs
                    computeTimeSeries()
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}
