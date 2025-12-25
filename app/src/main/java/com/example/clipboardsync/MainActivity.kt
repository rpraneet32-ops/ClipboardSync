package com.example.clipboardsync

import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.clipboardsync.data.ClipboardItem
import com.example.clipboardsync.service.ClipboardService
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent

class MainActivity : ComponentActivity() {

    private val viewModel: ClipboardViewModel by viewModels {
        ClipboardViewModelFactory((application as ClipboardApp).database.clipboardDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ClipboardScreen(viewModel) { startSyncService() }
                }
            }
        }
    }

    private fun startSyncService() {
        try {
            val intent = Intent(this, ClipboardService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun ClipboardScreen(viewModel: ClipboardViewModel, onStartService: () -> Unit) {
    val items by viewModel.allItems.collectAsState(initial = emptyList())
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // This tries to auto-sync when you open the app
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                checkForNewClip(context, viewModel, showToast = false)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Sync Control Panel", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // BUTTON 1: FORCE PASTE (The Hero Button)
        Button(
            onClick = { checkForNewClip(context, viewModel, showToast = true) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Force Paste From Clipboard")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            // BUTTON 2: START SERVICE (For Notification)
            Button(
                onClick = onStartService,
                modifier = Modifier.weight(1f).padding(end = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Text(" Service")
            }

            // BUTTON 3: CLEAR ALL (For Cleanup)
            Button(
                onClick = {
                    items.forEach { viewModel.deleteItem(it) }
                    Toast.makeText(context, "History Cleared", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f).padding(start = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Text(" Clear")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider()

        LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
            items(items) { item ->
                ClipboardRow(item) { viewModel.deleteItem(item) }
            }
        }
    }
}

fun checkForNewClip(context: Context, viewModel: ClipboardViewModel, showToast: Boolean) {
    try {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (!clipboard.hasPrimaryClip()) return

        val item = clipboard.primaryClip?.getItemAt(0)
        val text = item?.text?.toString()

        if (!text.isNullOrEmpty()) {
            // Insert into Database
            viewModel.insertItem(text)
            if (showToast) {
                Toast.makeText(context, "Pasted!", Toast.LENGTH_SHORT).show()
            }
        } else if (showToast) {
            Toast.makeText(context, "Clipboard is empty or not text", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        if (showToast) Toast.makeText(context, "Error reading clipboard", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun ClipboardRow(item: ClipboardItem, onDelete: () -> Unit) {
    val date = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(item.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.content, style = MaterialTheme.typography.bodyLarge)
                Text(text = date, style = MaterialTheme.typography.labelSmall)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}