package com.example.mindmatrix.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindmatrix.MindMatrixApp
import com.example.mindmatrix.data.local.entity.HarvestLogEntity
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val _logsState = mutableStateOf<List<HarvestLogEntity>>(emptyList())
    val logsState: State<List<HarvestLogEntity>> = _logsState

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val app = application as MindMatrixApp
    private val database = app.database
    private val sessionManager = app.sessionManager

    fun fetchHistory() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val userId = sessionManager.getUserId()
                if (userId != -1) {
                    val logs = database.harvestLogDao().getLogsForUser(userId)
                    _logsState.value = logs
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteLog(log: HarvestLogEntity) {
        viewModelScope.launch {
            try {
                database.harvestLogDao().deleteLog(log)
                fetchHistory() // Refresh the list
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun exportToCsv(context: android.content.Context) {
        val logs = _logsState.value
        if (logs.isEmpty()) {
            android.widget.Toast.makeText(context, "No history to export", android.widget.Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val csvContent = StringBuilder()
                csvContent.append("Batch ID,Hive ID,Date,Location,Floral Source,Quantity (kg),Grade,Notes\n")
                
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                logs.forEach { log ->
                    csvContent.append("${log.batchId},")
                    csvContent.append("${log.hiveId ?: ""},")
                    csvContent.append("${sdf.format(java.util.Date(log.date))},")
                    csvContent.append("\"${log.location.replace("\"", "'")}\",")
                    csvContent.append("\"${log.floralSource.replace("\"", "'")}\",")
                    csvContent.append("${log.quantity},")
                    csvContent.append("${log.grade},")
                    csvContent.append("\"${(log.notes ?: "").replace("\"", "'")}\"\n")
                }

                val fileName = "Jenu_Gumpu_History_${System.currentTimeMillis() % 10000}.csv"
                
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    val contentValues = android.content.ContentValues().apply {
                        put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                        put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "text/csv")
                        put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_DOWNLOADS)
                        put(android.provider.MediaStore.MediaColumns.IS_PENDING, 1)
                    }
                    
                    val resolver = context.contentResolver
                    val uri = resolver.insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    
                    uri?.let {
                        resolver.openOutputStream(it)?.use { outputStream ->
                            outputStream.write(csvContent.toString().toByteArray())
                        }
                        
                        contentValues.clear()
                        contentValues.put(android.provider.MediaStore.MediaColumns.IS_PENDING, 0)
                        resolver.update(it, contentValues, null, null)

                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                            android.widget.Toast.makeText(context, "Saved: $fileName", android.widget.Toast.LENGTH_SHORT).show()
                            
                            // Try to open the file immediately
                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                setDataAndType(it, "text/csv")
                                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // If no CSV viewer, at least we saved it
                            }
                        }
                    } ?: throw Exception("Failed to create MediaStore entry")
                } else {
                    // Legacy approach for older Android versions
                    val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
                    val file = java.io.File(downloadsDir, fileName)
                    file.writeText(csvContent.toString())
                    
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        android.widget.Toast.makeText(context, "Saved to Downloads", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    android.widget.Toast.makeText(context, "Export failed: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
