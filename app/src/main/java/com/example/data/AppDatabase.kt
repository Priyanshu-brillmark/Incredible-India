package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.models.BlogEntity
import com.example.data.models.BookmarkEntity
import com.example.data.models.CategoryEntity
import com.example.data.models.StateEntity
import com.example.data.models.DownloadEntity
import com.example.data.models.ReviewEntity

@Database(
    entities = [
        BlogEntity::class,
        CategoryEntity::class,
        StateEntity::class,
        BookmarkEntity::class,
        DownloadEntity::class,
        ReviewEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun blogDao(): BlogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "incredible_india_blogs_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
