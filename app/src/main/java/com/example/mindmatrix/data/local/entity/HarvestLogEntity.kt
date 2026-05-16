package com.example.mindmatrix.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "harvest_logs")
data class HarvestLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Int,
    val date: Long,
    val location: String,
    val quantity: Double,
    val floralSource: String,
    val grade: String,
    val batchId: String,
    val hiveId: String? = null,
    val notes: String? = null
)
