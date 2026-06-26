package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.BlogEntity
import com.example.data.models.BookmarkEntity
import com.example.data.models.CategoryEntity
import com.example.data.models.StateEntity
import com.example.data.models.DownloadEntity
import com.example.data.models.ReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlogDao {
    // --- BLOGS ---
    @Query("SELECT * FROM blogs ORDER BY publishedDate DESC")
    fun getAllBlogsFlow(): Flow<List<BlogEntity>>

    @Query("SELECT * FROM blogs WHERE isPublished = 1 ORDER BY publishedDate DESC")
    fun getPublishedBlogsFlow(): Flow<List<BlogEntity>>

    @Query("SELECT * FROM blogs WHERE id = :id")
    suspend fun getBlogById(id: Int): BlogEntity?

    @Query("SELECT * FROM blogs WHERE id = :id")
    fun getBlogByIdFlow(id: Int): Flow<BlogEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlog(blog: BlogEntity): Long

    @Update
    suspend fun updateBlog(blog: BlogEntity)

    @Query("DELETE FROM blogs WHERE id = :id")
    suspend fun deleteBlogById(id: Int)

    @Query("UPDATE blogs SET viewCount = viewCount + 1 WHERE id = :id")
    suspend fun incrementViewCount(id: Int)

    // --- CATEGORIES ---
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategoriesFlow(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategoryById(id: Int)

    // --- STATES ---
    @Query("SELECT * FROM states ORDER BY name ASC")
    fun getAllStatesFlow(): Flow<List<StateEntity>>

    @Query("SELECT * FROM states")
    suspend fun getAllStates(): List<StateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertState(state: StateEntity): Long

    @Query("DELETE FROM states WHERE id = :id")
    suspend fun deleteStateById(id: Int)

    // --- BOOKMARKS ---
    @Query("SELECT * FROM bookmarks ORDER BY bookmarkedAt DESC")
    fun getAllBookmarksFlow(): Flow<List<BookmarkEntity>>

    @Query("SELECT b.* FROM blogs b INNER JOIN bookmarks bm ON b.id = bm.blogId WHERE b.isPublished = 1 ORDER BY bm.bookmarkedAt DESC")
    fun getBookmarkedBlogsFlow(): Flow<List<BlogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE blogId = :blogId")
    suspend fun deleteBookmark(blogId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE blogId = :blogId)")
    fun isBookmarkedFlow(blogId: Int): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE blogId = :blogId)")
    suspend fun isBookmarked(blogId: Int): Boolean

    // --- DOWNLOADS (OFFLINE BLOGS) ---
    @Query("SELECT b.* FROM blogs b INNER JOIN downloads d ON b.id = d.blogId WHERE b.isPublished = 1 ORDER BY d.downloadedAt DESC")
    fun getDownloadedBlogsFlow(): Flow<List<BlogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: DownloadEntity)

    @Query("DELETE FROM downloads WHERE blogId = :blogId")
    suspend fun deleteDownload(blogId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM downloads WHERE blogId = :blogId)")
    fun isDownloadedFlow(blogId: Int): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM downloads WHERE blogId = :blogId)")
    suspend fun isDownloaded(blogId: Int): Boolean

    // --- REVIEWS & RATINGS ---
    @Query("SELECT * FROM reviews WHERE blogId = :blogId ORDER BY timestamp DESC")
    fun getReviewsForBlogFlow(blogId: Int): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Query("DELETE FROM reviews WHERE id = :id")
    suspend fun deleteReview(id: Int)
}
