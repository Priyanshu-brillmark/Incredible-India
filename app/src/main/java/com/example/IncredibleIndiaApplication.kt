package com.example

import android.app.Application
import com.example.data.AppDatabase
import com.example.data.BlogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class IncredibleIndiaApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { BlogRepository(database.blogDao()) }

    private val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        // Seed database if empty in a background coroutine
        applicationScope.launch {
            repository.seedDatabaseIfEmpty()
        }
    }
}
