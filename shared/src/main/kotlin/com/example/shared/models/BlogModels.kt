package com.example.shared.models

data class Blog(
    val id: Int = 0,
    val title: String,
    val subtitle: String,
    val authorName: String,
    val publishedDate: Long = System.currentTimeMillis(),
    val readTimeMin: Int = 5,
    val category: String,
    val stateName: String,
    val featuredImageUrl: String,
    val content: String,
    val tagsRaw: String = "",
    val galleryRaw: String = "",
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

data class Category(
    val id: Int = 0,
    val name: String,
    val iconName: String
)

data class State(
    val id: Int = 0,
    val name: String,
    val description: String,
    val imageUrl: String
)

data class Review(
    val id: Int = 0,
    val blogId: Int,
    val username: String,
    val rating: Int,
    val reviewText: String,
    val timestamp: Long = System.currentTimeMillis()
)
