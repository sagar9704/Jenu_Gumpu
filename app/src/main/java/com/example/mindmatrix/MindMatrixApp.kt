package com.example.mindmatrix

import android.app.Application
import androidx.room.Room
import com.example.mindmatrix.data.local.AppDatabase
import com.example.mindmatrix.data.local.SessionManager

class MindMatrixApp : Application() {
    lateinit var database: AppDatabase
        private set

    lateinit var sessionManager: SessionManager
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "mindmatrix_db"
        ).fallbackToDestructiveMigration().build()
        
        sessionManager = SessionManager(applicationContext)
    }
}
