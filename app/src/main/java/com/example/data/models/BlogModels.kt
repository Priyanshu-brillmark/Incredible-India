package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blogs")
data class BlogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val subtitle: String,
    val authorName: String,
    val publishedDate: Long = System.currentTimeMillis(),
    val readTimeMin: Int = 5,
    val category: String,
    val stateName: String,
    val featuredImageUrl: String,
    val content: String,
    val tagsRaw: String = "", // Semicolon separated tags
    val galleryRaw: String = "", // Semicolon separated image urls
    val isPublished: Boolean = true,
    val isFeatured: Boolean = false,
    val isTrending: Boolean = false,
    val isEditorsPick: Boolean = false,
    val seoTitle: String = "",
    val seoDescription: String = "",
    val viewCount: Int = 0
) {
    val tags: List<String>
        get() = if (tagsRaw.isBlank()) emptyList() else tagsRaw.split(";").map { it.trim() }.filter { it.isNotEmpty() }

    val gallery: List<String>
        get() = if (galleryRaw.isBlank()) emptyList() else galleryRaw.split(";").map { it.trim() }.filter { it.isNotEmpty() }
}

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val iconName: String
)

@Entity(tableName = "states")
data class StateEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val imageUrl: String
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val blogId: Int,
    val bookmarkedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey val blogId: Int,
    val downloadedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val blogId: Int,
    val username: String,
    val rating: Int, // 1 to 5 stars
    val reviewText: String,
    val timestamp: Long = System.currentTimeMillis()
)

