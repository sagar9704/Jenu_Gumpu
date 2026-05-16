package com.example.mindmatrix.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mindmatrix.data.local.entity.FloralSourceStock
import com.example.mindmatrix.data.local.entity.HarvestLogEntity

@Dao
interface HarvestLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: HarvestLogEntity): Long

    @Query("SELECT * FROM harvest_logs WHERE userId = :userId ORDER BY date DESC")
    suspend fun getLogsForUser(userId: Int): List<HarvestLogEntity>

    @Query("SELECT SUM(quantity) FROM harvest_logs WHERE userId = :userId")
    suspend fun getTotalQuantityForUser(userId: Int): Double?

    @Query("SELECT COUNT(*) FROM harvest_logs WHERE userId = :userId")
    suspend fun getEntryCountForUser(userId: Int): Int

    @Query("SELECT floralSource, SUM(quantity) as totalQuantity FROM harvest_logs WHERE userId = :userId GROUP BY floralSource")
    suspend fun getQuantityByFloralSource(userId: Int): List<FloralSourceStock>

    @androidx.room.Delete
    suspend fun deleteLog(log: HarvestLogEntity)
}
