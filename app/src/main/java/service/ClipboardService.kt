package com.example.clipboardsync.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.clipboardsync.ClipboardApp
import com.example.clipboardsync.data.ClipboardItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ClipboardService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 1. Create the Notification Channel (Required for Android 8+)
        val channelId = "clipboard_sync_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Clipboard Sync",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        // 2. Build the Notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Sync Active")
            .setContentText("Service is running...")
            .setSmallIcon(android.R.drawable.ic_menu_share)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        // 3. START FOREGROUND IMMEDIATELY (This stops the crash)
        try {
            startForeground(1, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 4. Attach the listener SAFELY after the service is stable
        setupClipboardListener()

        return START_STICKY
    }

    private fun setupClipboardListener() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.addPrimaryClipChangedListener {
            val text = clipboard.primaryClip?.getItemAt(0)?.text?.toString()
            if (!text.isNullOrEmpty()) {
                saveToDatabase(text)
            }
        }
    }

    private fun saveToDatabase(content: String) {
        serviceScope.launch {
            try {
                // Try saving to local DB
                val app = applicationContext as? ClipboardApp
                app?.database?.clipboardDao()?.insert(ClipboardItem(content = content))

                // Try saving to Cloud
                val db = FirebaseFirestore.getInstance()
                val data = hashMapOf(
                    "content" to content,
                    "timestamp" to System.currentTimeMillis(),
                    "device" to android.os.Build.MODEL
                )
                db.collection("clips").add(data)
            } catch (e: Exception) {
                // Ignore errors for now to keep app alive
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
    }
}