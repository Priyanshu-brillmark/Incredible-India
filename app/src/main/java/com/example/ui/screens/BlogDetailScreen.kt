package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.foundation.layout.statusBarsPadding
import android.content.Context
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.models.BlogEntity
import com.example.ui.theme.AshokaNavy
import com.example.ui.theme.IndiaGreen
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.BentoBorderColor
import com.example.ui.theme.BentoDarkBorderColor
import com.example.ui.viewmodel.BlogViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BlogDetailScreen(
    blogId: Int,
    viewModel: BlogViewModel,
    onBack: () -> Unit,
    onNavigateToBlog: (Int) -> Unit
) {
    val context = LocalContext.current
    var blog by remember { mutableStateOf<BlogEntity?>(null) }
    val allPublishedBlogs by viewModel.publishedBlogs.collectAsState()

    // Load active blog and increment view count
    LaunchedEffect(blogId) {
        viewModel.getBlogById(blogId) { loadedBlog ->
            blog = loadedBlog
            if (loadedBlog != null) {
                viewModel.incrementViewCount(loadedBlog.id)
            }
        }
    }

    if (blog == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading beautiful travel story...")
        }
        return
    }

    val activeBlog = blog!!
    val isBookmarked by viewModel.isBookmarkedFlow(activeBlog.id).collectAsState(initial = false)
    val isDownloaded by viewModel.isDownloadedFlow(activeBlog.id).collectAsState(initial = false)

    val reviews by viewModel.getReviewsForBlogFlow(activeBlog.id).collectAsState(initial = emptyList())
    val averageRating = if (reviews.isEmpty()) 0.0 else reviews.map { it.rating }.average()
    val reviewsCount = reviews.size

    val session by viewModel.userSession.collectAsState()
    val username = session?.username ?: "Anonymous Traveler"

    var ratingInput by remember { mutableStateOf(5) }
    var reviewTextInput by remember { mutableStateOf("") }
    var showAuthorDialog by remember { mutableStateOf(false) }

    val wordCount = activeBlog.content.split(Regex("\\s+")).filter { it.isNotBlank() }.size
    val calculatedReadTime = maxOf(1, (wordCount + 199) / 200)

    // Filter related recommendations from same state or category
    val relatedBlogs = allPublishedBlogs.filter {
        (it.category == activeBlog.category || it.stateName == activeBlog.stateName) && it.id != activeBlog.id
    }

    val listState = rememberLazyListState()
    val scrollProgress by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isEmpty()) {
                0f
            } else {
                val firstItem = visibleItemsInfo.first()
                val totalItems = layoutInfo.totalItemsCount
                val lastItem = visibleItemsInfo.last()
                
                val totalHeight = (totalItems - 1) * 100f
                val currentScroll = firstItem.index * 100f + (-firstItem.offset.toFloat() / firstItem.size * 100f)
                
                val progress = if (lastItem.index == totalItems - 1 && lastItem.offset + lastItem.size <= layoutInfo.viewportEndOffset) {
                    1f
                } else {
                    currentScroll / totalHeight.coerceAtLeast(1f)
                }
                progress.coerceIn(0f, 1f)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .testTag("blog_detail_root"),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
        // 1. Full-bleed Hero Image with curved bottom & floating overlays
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(activeBlog.featuredImageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = activeBlog.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark top & bottom gradients
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.5f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.75f)
                                )
                            )
                        )
                )

                // Back Button & Bookmark Button Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 44.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go back",
                            tint = Color.White
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Share Button
                        IconButton(
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "Link copied to clipboard! Share with friends: https://incredibleindia.gov.in/blogs/${activeBlog.id}",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                .testTag("detail_share_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White
                            )
                        }

                        // Bookmark Toggle
                        IconButton(
                            onClick = {
                                viewModel.toggleBookmark(activeBlog.id)
                                val message = if (isBookmarked) "Removed from Saved Collection" else "Saved to offline reading collection!"
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                .testTag("detail_bookmark_button")
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = SaffronPrimary
                            )
                        }

                        // Download Toggle for Offline
                        IconButton(
                            onClick = {
                                viewModel.toggleDownload(activeBlog.id)
                                val message = if (isDownloaded) "Removed from Offline Blogs" else "Downloaded for offline reading!"
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                .testTag("detail_download_button")
                        ) {
                            Icon(
                                imageVector = if (isDownloaded) Icons.Default.CloudDone else Icons.Default.Download,
                                contentDescription = "Download Offline",
                                tint = if (isDownloaded) IndiaGreen else Color.White
                            )
                        }
                    }
                }

                // Floating Metadata chips
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(SaffronPrimary, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = activeBlog.category,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(IndiaGreen, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = activeBlog.stateName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }

        // 2. Journal Headers
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Text(
                    text = activeBlog.title,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 26.sp,
                        lineHeight = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = AshokaNavy
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = activeBlog.subtitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 17.sp,
                        lineHeight = 24.sp
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Ratings Overview
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (reviewsCount > 0) String.format("%.1f", averageRating) else "No ratings yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AshokaNavy
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "•  $reviewsCount ${if (reviewsCount == 1) "review" else "reviews"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Author, date, reading time card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Author detail
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showAuthorDialog = true }
                                .padding(4.dp)
                                .testTag("blog_author_clickable")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(SaffronPrimary.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = SaffronPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = activeBlog.authorName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AshokaNavy
                                )
                                Text(
                                    text = "Travel Journalist",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }
                        }

                        // Publication date & read time
                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = formatDate(activeBlog.publishedDate),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.testTag("reading_time_row")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = "Estimated Reading Time",
                                    tint = SaffronPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$calculatedReadTime min read ($wordCount words)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SaffronPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.testTag("estimated_reading_time")
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Rich Formatted Markdown Content
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                val paragraphs = activeBlog.content.split("\n")
                paragraphs.forEach { paragraph ->
                    val trimmed = paragraph.trim()
                    if (trimmed.startsWith("###")) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = trimmed.removePrefix("###").trim(),
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 19.sp),
                            fontWeight = FontWeight.Bold,
                            color = AshokaNavy,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    } else if (trimmed.startsWith("##")) {
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = trimmed.removePrefix("##").trim(),
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 21.sp),
                            fontWeight = FontWeight.Bold,
                            color = AshokaNavy,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    } else if (trimmed.startsWith("- ")) {
                        Row(
                            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(text = "• ", fontWeight = FontWeight.Bold, color = SaffronPrimary, fontSize = 16.sp)
                            Text(
                                text = trimmed.removePrefix("- ").trim(),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    lineHeight = 24.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
                                )
                            )
                        }
                    } else if (trimmed.isNotBlank()) {
                        Text(
                            text = trimmed,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                lineHeight = 26.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f)
                            ),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                }
            }
        }

        // 4. Tags Flow Box
        if (activeBlog.tags.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    activeBlog.tags.forEach { tag ->
                        Text(
                            text = "#$tag",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = IndiaGreen,
                            modifier = Modifier
                                .background(IndiaGreen.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // 5. Image Gallery
        if (activeBlog.gallery.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(vertical = 16.dp)) {
                    Text(
                        text = "Image Gallery",
                        style = MaterialTheme.typography.titleLarge,
                        color = AshokaNavy,
                        modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 12.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(activeBlog.gallery) { imageUrl ->
                            Card(
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Gallery item",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5.5 Social Sharing Component
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .testTag("social_sharing_card"),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BentoBorderColor),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Spread the Wanderlust",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AshokaNavy
                    )
                    Text(
                        text = "Share this wonderful destination with your friends and family!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Copy Link Action
                        IconButtonWithLabel(
                            icon = Icons.Default.ContentCopy,
                            label = "Copy Link",
                            backgroundColor = SaffronPrimary.copy(alpha = 0.1f),
                            iconColor = SaffronPrimary,
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                val clip = android.content.ClipData.newPlainText("Incredible India Travel Blog", "https://incredibleindia.gov.in/blogs/${activeBlog.id}")
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Link copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("share_copy_link")
                        )

                        // 2. WhatsApp Mock
                        IconButtonWithLabel(
                            icon = Icons.Default.ChatBubble,
                            label = "WhatsApp",
                            backgroundColor = IndiaGreen.copy(alpha = 0.1f),
                            iconColor = IndiaGreen,
                            onClick = {
                                Toast.makeText(context, "Sharing story via WhatsApp...", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("share_whatsapp")
                        )

                        // 3. Twitter/X Mock
                        IconButtonWithLabel(
                            icon = Icons.Default.Public,
                            label = "Twitter/X",
                            backgroundColor = Color.Black.copy(alpha = 0.05f),
                            iconColor = Color.Black,
                            onClick = {
                                Toast.makeText(context, "Opening Twitter/X to share story...", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("share_twitter")
                        )

                        // 4. Facebook Mock
                        IconButtonWithLabel(
                            icon = Icons.Default.Send,
                            label = "Direct Share",
                            backgroundColor = AshokaNavy.copy(alpha = 0.1f),
                            iconColor = AshokaNavy,
                            onClick = {
                                Toast.makeText(context, "Opening share menu...", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("share_facebook")
                        )
                    }
                }
            }
        }

        // 6. Related Articles Recommendations
        if (relatedBlogs.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(vertical = 16.dp)) {
                    Text(
                        text = "Related Articles",
                        style = MaterialTheme.typography.titleLarge,
                        color = AshokaNavy,
                        modifier = Modifier
                            .padding(start = 24.dp, end = 24.dp, bottom = 12.dp)
                            .testTag("related_articles_title")
                    )

                    LazyRow(
                        modifier = Modifier.testTag("related_articles_carousel"),
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(relatedBlogs) { rBlog ->
                            Card(
                                modifier = Modifier
                                    .width(220.dp)
                                    .clickable { onNavigateToBlog(rBlog.id) }
                                    .testTag("related_article_card_${rBlog.id}"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(rBlog.featuredImageUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(110.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = rBlog.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 2,
                                            color = AshokaNavy
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${rBlog.category} • ${rBlog.readTimeMin} min",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SaffronPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 7. Write a Review Form Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, if (isSystemInDarkTheme()) BentoDarkBorderColor else BentoBorderColor),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Rate your reading experience",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AshokaNavy
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        (1..5).forEach { starIndex ->
                            val isSelected = starIndex <= ratingInput
                            Icon(
                                imageVector = if (isSelected) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "$starIndex stars",
                                tint = if (isSelected) Color(0xFFFFB300) else Color.Gray.copy(alpha = 0.4f),
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { ratingInput = starIndex }
                                    .testTag("star_select_$starIndex")
                            )
                        }
                    }

                    androidx.compose.material3.OutlinedTextField(
                        value = reviewTextInput,
                        onValueChange = { reviewTextInput = it },
                        placeholder = { Text("Share your thoughts on this destination...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("review_input_field"),
                        maxLines = 4,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Button(
                        onClick = {
                            if (reviewTextInput.isBlank()) {
                                Toast.makeText(context, "Please write some thoughts first", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.addReview(
                                    blogId = activeBlog.id,
                                    username = username,
                                    rating = ratingInput,
                                    reviewText = reviewTextInput
                                )
                                reviewTextInput = ""
                                ratingInput = 5
                                Toast.makeText(context, "Thank you for your valuable feedback!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("submit_review_button")
                    ) {
                        Text("Submit Review", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // 8. Recent Reviews List
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Traveler Reviews (${reviews.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = AshokaNavy,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (reviews.isEmpty()) {
                    Text(
                        text = "Be the first to share your thoughts on this blog!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        reviews.forEach { r ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, if (isSystemInDarkTheme()) BentoDarkBorderColor else BentoBorderColor),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = r.username,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = AshokaNavy
                                        )
                                        
                                        Row {
                                            (1..5).forEach { starIndex ->
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = null,
                                                    tint = if (starIndex <= r.rating) Color(0xFFFFB300) else Color.Gray.copy(alpha = 0.3f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = r.reviewText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = formatDate(r.timestamp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        }

        // Visual scroll progress indicator bar at the top
        LinearProgressIndicator(
            progress = scrollProgress,
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(4.dp)
                .align(Alignment.TopCenter)
                .testTag("scroll_progress_bar"),
            color = SaffronPrimary,
            trackColor = Color.Transparent
        )

        // Author Biography Dialog
        if (showAuthorDialog) {
            val authorBlogsCount = allPublishedBlogs.count { it.authorName == activeBlog.authorName }
            val authorBios = mapOf(
                "Arjun Mehta" to "Arjun is a cultural historian and travel photographer based in Varanasi. He has spent over a decade documenting India's ancient festivals, temples, and spiritual heritage.",
                "Ananya Sharma" to "Ananya is a culinary anthropologist and travel journalist. She loves exploring local spice markets, interviewing traditional chefs, and unearthing forgotten recipes from across India.",
                "Vikram Singh" to "Vikram is an avid trekker, wildlife enthusiast, and adventure travel blogger. From the high-altitude passes of Ladakh to the tropical rainforests of Kerala, he is always on the hunt for the next thrill.",
                "Priya Nair" to "Priya is an architect and heritage conservationist. She specializes in writing about ancient Indian rock-cut temples, Mughal forts, and Dravidian temple architecture.",
                "Rohan Gupta" to "Rohan is an eco-travel advocate and digital nomad. He focuses on sustainable travel practices, community-based homestays, and highlighting off-the-beaten-path villages."
            )
            val bio = authorBios[activeBlog.authorName] ?: "${activeBlog.authorName} is a passionate travel storyteller, dedicated to uncovering the unique cultures, stunning destinations, and culinary arts of Incredible India."

            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showAuthorDialog = false },
                modifier = Modifier.testTag("author_bio_dialog"),
                shape = RoundedCornerShape(24.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                title = null,
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. Large Profile Circular Picture/Initial
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(SaffronPrimary.copy(alpha = 0.15f), CircleShape)
                                .border(BorderStroke(2.dp, SaffronPrimary), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = activeBlog.authorName.firstOrNull()?.toString() ?: "I",
                                style = MaterialTheme.typography.displaySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SaffronPrimary
                                )
                            )
                        }

                        // 2. Author Name & Role
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = activeBlog.authorName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = AshokaNavy,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.testTag("author_dialog_name")
                                )
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified Writer",
                                    tint = IndiaGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = "Incredible India Contributor",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // 3. Stats Section
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Stat 1: Total Blogs Count
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("author_stat_blogs_count"),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "$authorBlogsCount",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = AshokaNavy
                                    )
                                    Text(
                                        text = "Articles",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }
                            }

                            // Stat 2: Rating/Level
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Level 5",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = SaffronPrimary
                                    )
                                    Text(
                                        text = "Explorer Status",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }

                        // 4. Author Biography
                        Text(
                            text = bio,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .testTag("author_dialog_bio")
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { showAuthorDialog = false },
                        colors = ButtonDefaults.textButtonColors(contentColor = SaffronPrimary),
                        modifier = Modifier.testTag("author_dialog_close")
                    ) {
                        Text("Close", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Composable
fun IconButtonWithLabel(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    backgroundColor: Color,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(backgroundColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
