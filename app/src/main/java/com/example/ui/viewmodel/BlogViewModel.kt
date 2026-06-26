package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.BlogRepository
import com.example.data.models.BlogEntity
import com.example.data.models.CategoryEntity
import com.example.data.models.StateEntity
import com.example.data.models.ReviewEntity
import android.app.Application
import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class UserSession(
    val username: String,
    val isAdmin: Boolean
)

data class TravelNotification(
    val id: Int,
    val title: String,
    val description: String,
    val timeAgo: String,
    val category: String // "Festival", "New Post", "Travel Advisory"
)

class BlogViewModel(private val repository: BlogRepository, private val application: Application) : ViewModel() {

    // --- Recent Searches ---
    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches = _recentSearches.asStateFlow()

    fun addRecentSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return
        val currentList = _recentSearches.value.toMutableList()
        currentList.remove(trimmed)
        currentList.add(0, trimmed)
        val updatedList = currentList.take(8)
        _recentSearches.value = updatedList
        
        val sharedPrefs = application.getSharedPreferences("incredible_india_prefs", Context.MODE_PRIVATE)
        val savedString = updatedList.joinToString("|||")
        sharedPrefs.edit().putString("recent_searches_list", savedString).apply()
    }

    fun removeRecentSearch(query: String) {
        val currentList = _recentSearches.value.toMutableList()
        currentList.remove(query)
        _recentSearches.value = currentList
        
        val sharedPrefs = application.getSharedPreferences("incredible_india_prefs", Context.MODE_PRIVATE)
        val savedString = currentList.joinToString("|||")
        sharedPrefs.edit().putString("recent_searches_list", savedString).apply()
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
        val sharedPrefs = application.getSharedPreferences("incredible_india_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().remove("recent_searches_list").apply()
    }

    private fun loadRecentSearches() {
        val sharedPrefs = application.getSharedPreferences("incredible_india_prefs", Context.MODE_PRIVATE)
        val savedString = sharedPrefs.getString("recent_searches_list", "") ?: ""
        if (savedString.isNotBlank()) {
            _recentSearches.value = savedString.split("|||").filter { it.isNotBlank() }
        }
    }

    // --- Newsletter Subscription (Mock Backend) ---
    private val _newsletterSubscribed = MutableStateFlow(false)
    val newsletterSubscribed = _newsletterSubscribed.asStateFlow()

    private val _isSubscribing = MutableStateFlow(false)
    val isSubscribing = _isSubscribing.asStateFlow()

    // --- Blogs Loading State ---
    private val _isBlogsLoading = MutableStateFlow(true)
    val isBlogsLoading = _isBlogsLoading.asStateFlow()

    fun subscribeToNewsletter(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            onError("Please enter a valid email address.")
            return
        }
        viewModelScope.launch {
            _isSubscribing.value = true
            delay(1500) // Mock API network call latency
            _newsletterSubscribed.value = true
            _isSubscribing.value = false
            onSuccess()
        }
    }

    // --- Flows from Room DB ---
    val allBlogs: StateFlow<List<BlogEntity>> = repository.allBlogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val publishedBlogs: StateFlow<List<BlogEntity>> = repository.publishedBlogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = repository.categories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val states: StateFlow<List<StateEntity>> = repository.states
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedBlogs: StateFlow<List<BlogEntity>> = repository.bookmarkedBlogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val downloadedBlogs: StateFlow<List<BlogEntity>> = repository.downloadedBlogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Active Filters & Search ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _selectedState = MutableStateFlow<String?>(null)
    val selectedState = _selectedState.asStateFlow()

    // Combined filtered published blogs for Search screen and home discovery
    val filteredPublishedBlogs: StateFlow<List<BlogEntity>> = combine(
        publishedBlogs,
        _searchQuery,
        _selectedCategory,
        _selectedState
    ) { blogs, query, category, state ->
        blogs.filter { blog ->
            val matchesQuery = query.isBlank() || 
                    blog.title.contains(query, ignoreCase = true) ||
                    blog.subtitle.contains(query, ignoreCase = true) ||
                    blog.content.contains(query, ignoreCase = true) ||
                    blog.tagsRaw.contains(query, ignoreCase = true)

            val matchesCategory = category == null || blog.category.equals(category, ignoreCase = true)
            val matchesState = state == null || blog.stateName.equals(state, ignoreCase = true)

            matchesQuery && matchesCategory && matchesState
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- User Session ---
    private val _userSession = MutableStateFlow<UserSession?>(null)
    val userSession = _userSession.asStateFlow()

    // --- Notifications ---
    private val _notifications = MutableStateFlow<List<TravelNotification>>(emptyList())
    val notifications = _notifications.asStateFlow()

    // --- Initializer ---
    init {
        loadSampleNotifications()
        loadRecentSearches()
        viewModelScope.launch {
            delay(1500)
            _isBlogsLoading.value = false
        }
    }

    private fun loadSampleNotifications() {
        _notifications.value = listOf(
            TravelNotification(
                id = 1,
                title = "Holi Celebration Guide",
                description = "The comprehensive festival safety and photo-walk guidelines for Vrindavan are now live!",
                timeAgo = "2 hours ago",
                category = "Festival"
            ),
            TravelNotification(
                id = 2,
                title = "Monsoon Travel Advisory",
                description = "High alert in parts of Western Ghats. Avoid trekking routes in Munnar this weekend.",
                timeAgo = "1 day ago",
                category = "Travel Advisory"
            ),
            TravelNotification(
                id = 3,
                title = "New Hidden Gem Discovered",
                description = "Read about 'The Secret Cave Temples of Badami' posted by editor Ananya.",
                timeAgo = "3 days ago",
                category = "New Post"
            )
        )
    }

    // --- Filtering Setters ---
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(categoryName: String?) {
        _selectedCategory.value = categoryName
    }

    fun selectState(stateName: String?) {
        _selectedState.value = stateName
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedCategory.value = null
        _selectedState.value = null
    }

    // --- Bookmarks Operations ---
    fun toggleBookmark(blogId: Int) {
        viewModelScope.launch {
            repository.toggleBookmark(blogId)
        }
    }

    fun isBookmarkedFlow(blogId: Int): Flow<Boolean> {
        return repository.isBookmarkedFlow(blogId)
    }

    // --- Blog View Analytics ---
    fun incrementViewCount(blogId: Int) {
        viewModelScope.launch {
            repository.incrementViewCount(blogId)
        }
    }

    fun getBlogById(blogId: Int, callback: (BlogEntity?) -> Unit) {
        viewModelScope.launch {
            val blog = repository.getBlogById(blogId)
            callback(blog)
        }
    }

    // --- Auth Actions ---
    fun login(username: String, password: String, asAdmin: Boolean): Boolean {
        if (asAdmin) {
            if (username == "admin" && password == "admin123") {
                _userSession.value = UserSession(username = "Admin Incredible", isAdmin = true)
                return true
            }
        } else {
            if (username.isNotBlank() && password.length >= 4) {
                _userSession.value = UserSession(username = username, isAdmin = false)
                return true
            }
        }
        return false
    }

    fun register(username: String, email: String, password: String): Boolean {
        if (username.isNotBlank() && password.length >= 4) {
            _userSession.value = UserSession(username = username, isAdmin = false)
            return true
        }
        return false
    }

    fun logout() {
        _userSession.value = null
    }

    // --- Admin: Blog CRUD ---
    fun saveBlog(
        id: Int,
        title: String,
        subtitle: String,
        author: String,
        category: String,
        state: String,
        imageUrl: String,
        content: String,
        tags: String,
        gallery: String,
        isPublished: Boolean,
        isFeatured: Boolean,
        isTrending: Boolean,
        isEditorsPick: Boolean,
        seoTitle: String,
        seoDescription: String,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (id == 0) {
                    val newBlog = BlogEntity(
                        title = title,
                        subtitle = subtitle,
                        authorName = author,
                        category = category,
                        stateName = state,
                        featuredImageUrl = imageUrl,
                        content = content,
                        tagsRaw = tags,
                        galleryRaw = gallery,
                        isPublished = isPublished,
                        isFeatured = isFeatured,
                        isTrending = isTrending,
                        isEditorsPick = isEditorsPick,
                        seoTitle = seoTitle,
                        seoDescription = seoDescription
                    )
                    repository.insertBlog(newBlog)
                } else {
                    val existingBlog = repository.getBlogById(id)
                    if (existingBlog != null) {
                        val updatedBlog = existingBlog.copy(
                            title = title,
                            subtitle = subtitle,
                            authorName = author,
                            category = category,
                            stateName = state,
                            featuredImageUrl = imageUrl,
                            content = content,
                            tagsRaw = tags,
                            galleryRaw = gallery,
                            isPublished = isPublished,
                            isFeatured = isFeatured,
                            isTrending = isTrending,
                            isEditorsPick = isEditorsPick,
                            seoTitle = seoTitle,
                            seoDescription = seoDescription
                        )
                        repository.updateBlog(updatedBlog)
                    }
                }
                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }

    fun deleteBlog(id: Int, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteBlog(id)
            onComplete()
        }
    }

    // --- Admin: Category CRUD ---
    fun addCategory(name: String, iconName: String) {
        viewModelScope.launch {
            repository.insertCategory(CategoryEntity(name = name, iconName = iconName))
        }
    }

    fun deleteCategory(id: Int) {
        viewModelScope.launch {
            repository.deleteCategory(id)
        }
    }

    // --- Admin: State CRUD ---
    fun addState(name: String, description: String, imageUrl: String) {
        viewModelScope.launch {
            repository.insertState(StateEntity(name = name, description = description, imageUrl = imageUrl))
        }
    }

    fun deleteState(id: Int) {
        viewModelScope.launch {
            repository.deleteState(id)
        }
    }

    // --- Downloads Operations ---
    fun toggleDownload(blogId: Int) {
        viewModelScope.launch {
            repository.toggleDownload(blogId)
        }
    }

    fun isDownloadedFlow(blogId: Int): Flow<Boolean> {
        return repository.isDownloadedFlow(blogId)
    }

    // --- Reviews & Ratings ---
    fun getReviewsForBlogFlow(blogId: Int): Flow<List<ReviewEntity>> {
        return repository.getReviewsForBlogFlow(blogId)
    }

    fun addReview(blogId: Int, username: String, rating: Int, reviewText: String) {
        viewModelScope.launch {
            repository.addReview(
                ReviewEntity(
                    blogId = blogId,
                    username = username,
                    rating = rating,
                    reviewText = reviewText
                )
            )
        }
    }
}

class BlogViewModelFactory(
    private val repository: BlogRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BlogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BlogViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
