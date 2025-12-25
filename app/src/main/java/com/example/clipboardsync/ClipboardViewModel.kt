package com.example.clipboardsync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.clipboardsync.data.ClipboardDao
import com.example.clipboardsync.data.ClipboardItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ClipboardViewModel(private val dao: ClipboardDao) : ViewModel() {

    val allItems: Flow<List<ClipboardItem>> = dao.getAllItems()

    fun deleteItem(item: ClipboardItem) {
        viewModelScope.launch { dao.delete(item) }
    }

    // UPDATED: Now saves to BOTH Local DB and Cloud Firestore
    fun insertItem(content: String) {
        viewModelScope.launch {
            // 1. Save Locally (Instant UI update)
            dao.insert(ClipboardItem(content = content))

            // 2. Save to Cloud (So Windows can see it)
            try {
                val db = FirebaseFirestore.getInstance()
                val clipData = hashMapOf(
                    "content" to content,
                    "timestamp" to System.currentTimeMillis(),
                    "device" to android.os.Build.MODEL
                )
                db.collection("clips").add(clipData)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

class ClipboardViewModelFactory(private val dao: ClipboardDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClipboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClipboardViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}