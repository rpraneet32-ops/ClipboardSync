package com.example.clipboardsync

import android.app.Application
import androidx.room.Room
import com.example.clipboardsync.data.ClipboardDatabase

class ClipboardApp : Application() {
    // This variable holds the database connection for the whole app
    lateinit var database: ClipboardDatabase

    override fun onCreate() {
        super.onCreate()
        // Initialize the Room Database
        database = Room.databaseBuilder(
            applicationContext,
            ClipboardDatabase::class.java,
            "clipboard-database"
        ).build()
    }
}