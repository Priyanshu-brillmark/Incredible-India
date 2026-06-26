package com.example.desktop.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.desktop.theme.AshokaNavy
import com.example.desktop.theme.SaffronPrimary
import com.example.shared.data.InMemoryBlogRepository
import com.example.shared.models.Review
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch

@Composable
fun BlogDetailScreen(
    blogId: Int,
    repository: InMemoryBlogRepository,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onOpenBlog: (Int) -> Unit
) {
    val blog by repository.getBlogByIdFlow(blogId).collectAsState(initial = null)
    val reviews by repository.getReviewsForBlogFlow(blogId).collectAsState(initial = emptyList())
    val isBookmarked by repository.isBookmarkedFlow(blogId).collectAsState(initial = false)
    val scope = rememberCoroutineScope()
    var rating by remember { mutableIntStateOf(5) }
    var reviewText by remember { mutableStateOf("") }

    LaunchedEffect(blogId) {
        repository.incrementViewCount(blogId)
    }

    val activeBlog = blog ?: return

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                IconButton(
                    onClick = {
                        scope.launch { repository.toggleBookmark(blogId) }
                    }
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) SaffronPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        item {
            KamelImage(
                resource = asyncPainterResource(activeBlog.featuredImageUrl),
                contentDescription = activeBlog.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                contentScale = ContentScale.Crop
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = activeBlog.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = AshokaNavy
                )
                Text(
                    text = activeBlog.subtitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${activeBlog.authorName} · ${activeBlog.readTimeMin} min read",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Text(
                text = activeBlog.content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            ReviewComposer(
                rating = rating,
                reviewText = reviewText,
                onRatingChange = { rating = it },
                onReviewTextChange = { reviewText = it },
                onSubmit = {
                    scope.launch {
                        repository.addReview(
                            Review(
                                blogId = blogId,
                                username = "Desktop Reader",
                                rating = rating,
                                reviewText = reviewText
                            )
                        )
                        reviewText = ""
                    }
                }
            )
        }

        items(reviews) { review ->
            ReviewCard(review = review)
        }
    }
}

@Composable
private fun ReviewComposer(
    rating: Int,
    reviewText: String,
    onRatingChange: (Int) -> Unit,
    onReviewTextChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Leave a review", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                (1..5).forEach { star ->
                    Text(
                        text = if (star <= rating) "★" else "☆",
                        modifier = Modifier.clickable { onRatingChange(star) },
                        color = SaffronPrimary,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
            OutlinedTextField(
                value = reviewText,
                onValueChange = onReviewTextChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Your review") },
                minLines = 3
            )
            Button(onClick = onSubmit, enabled = reviewText.isNotBlank()) {
                Text("Submit review")
            }
        }
    }
}

@Composable
private fun ReviewCard(review: Review) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(review.username, fontWeight = FontWeight.Bold)
            Text("Rating: ${review.rating}/5", color = SaffronPrimary)
            Text(review.reviewText)
        }
    }
}
