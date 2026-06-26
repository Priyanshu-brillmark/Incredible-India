package com.example.shared.data

import com.example.shared.models.Blog
import com.example.shared.models.Category
import com.example.shared.models.Review
import com.example.shared.models.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryBlogRepository {
    private val mutex = Mutex()
    private val blogs = MutableStateFlow(BlogSeedData.blogs())
    private val categories = MutableStateFlow(BlogSeedData.categories())
    private val states = MutableStateFlow(BlogSeedData.states())
    private val bookmarks = MutableStateFlow<Set<Int>>(emptySet())
    private val reviews = MutableStateFlow<List<Review>>(emptyList())

    val publishedBlogs: Flow<List<Blog>> = blogs.map { list ->
        list.filter { it.isPublished }.sortedByDescending { it.publishedDate }
    }

    val allCategories: Flow<List<Category>> = categories
    val allStates: Flow<List<State>> = states

    val bookmarkedBlogs: Flow<List<Blog>> = blogs.map { list ->
        val ids = bookmarks.value
        list.filter { it.id in ids }.sortedByDescending { it.publishedDate }
    }

    fun getBlogByIdFlow(id: Int): Flow<Blog?> = blogs.map { list -> list.find { it.id == id } }

    fun getReviewsForBlogFlow(blogId: Int): Flow<List<Review>> = reviews.map { list ->
        list.filter { it.blogId == blogId }.sortedByDescending { it.timestamp }
    }

    fun isBookmarkedFlow(blogId: Int): Flow<Boolean> = bookmarks.map { it.contains(blogId) }

    suspend fun getBlogById(id: Int): Blog? = mutex.withLock {
        blogs.value.find { it.id == id }
    }

    suspend fun incrementViewCount(id: Int) = mutex.withLock {
        blogs.update { list ->
            list.map { blog ->
                if (blog.id == id) blog.copy(viewCount = blog.viewCount + 1) else blog
            }
        }
    }

    suspend fun toggleBookmark(blogId: Int) = mutex.withLock {
        bookmarks.update { current ->
            if (blogId in current) current - blogId else current + blogId
        }
    }

    suspend fun addReview(review: Review) = mutex.withLock {
        val nextId = (reviews.value.maxOfOrNull { it.id } ?: 0) + 1
        reviews.update { it + review.copy(id = nextId) }
    }

    fun searchBlogs(query: String): List<Blog> {
        val normalized = query.trim().lowercase()
        if (normalized.isBlank()) return blogs.value.filter { it.isPublished }
        return blogs.value.filter { blog ->
            blog.isPublished && (
                blog.title.lowercase().contains(normalized) ||
                    blog.subtitle.lowercase().contains(normalized) ||
                    blog.category.lowercase().contains(normalized) ||
                    blog.stateName.lowercase().contains(normalized) ||
                    blog.tags.any { it.lowercase().contains(normalized) }
                )
        }
    }
}
