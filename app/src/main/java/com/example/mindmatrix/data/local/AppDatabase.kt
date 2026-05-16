package com.example.mindmatrix.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mindmatrix.data.local.dao.HarvestLogDao
import com.example.mindmatrix.data.local.dao.UserDao
import com.example.mindmatrix.data.local.entity.HarvestLogEntity
import com.example.mindmatrix.data.local.entity.UserEntity

@Database(entities = [UserEntity::class, HarvestLogEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun harvestLogDao(): HarvestLogDao
}
