package com.example.mindmatrix.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindmatrix.MindMatrixApp
import com.example.mindmatrix.data.local.entity.HarvestLogEntity
import kotlinx.coroutines.launch

class AddHarvestViewModel(application: Application) : AndroidViewModel(application) {
    private val _isSubmitting = mutableStateOf(false)
    val isSubmitting: State<Boolean> = _isSubmitting

    private val _isSuccess = mutableStateOf(false)
    val isSuccess: State<Boolean> = _isSuccess

    private val app = application as MindMatrixApp
    private val database = app.database
    private val sessionManager = app.sessionManager

    fun submitHarvest(
        location: String,
        quantity: Double,
        floralSource: String,
        grade: String,
        date: Long,
        hiveId: String?,
        batchId: String?,
        notes: String?
    ) {
        _isSubmitting.value = true
        
        viewModelScope.launch {
            try {
                val userId = sessionManager.getUserId()
                if (userId != -1) {
                    val log = HarvestLogEntity(
                        userId = userId,
                        date = date,
                        location = location,
                        quantity = quantity,
                        floralSource = floralSource,
                        grade = grade,
                        batchId = if (batchId.isNullOrEmpty()) "BATCH-${System.currentTimeMillis() % 10000}" else batchId,
                        hiveId = hiveId,
                        notes = notes
                    )
                    
                    database.harvestLogDao().insertLog(log)
                    _isSuccess.value = true
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun resetSuccess() {
        _isSuccess.value = false
    }
}
