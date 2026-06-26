package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FilterHdr
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.models.BlogEntity
import com.example.data.models.CategoryEntity
import com.example.data.models.StateEntity
import com.example.ui.theme.AshokaNavy
import com.example.ui.theme.BentoBorderColor
import com.example.ui.theme.BentoDarkBorderColor
import com.example.ui.theme.IndiaGreen
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.WarmSandCard
import com.example.ui.theme.EarthyCharcoal
import com.example.ui.viewmodel.BlogViewModel
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border

@Composable
fun HomeScreen(
    viewModel: BlogViewModel,
    onNavigateToBlog: (Int) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToStates: () -> Unit
) {
    val blogs by viewModel.publishedBlogs.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val states by viewModel.states.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val isBlogsLoading by viewModel.isBlogsLoading.collectAsState()

    val featuredBlogs = blogs.filter { it.isFeatured }
    val trendingBlogs = blogs.filter { it.isTrending }.sortedByDescending { it.viewCount }
    val editorsPicks = blogs.filter { it.isEditorsPick }
    val recentBlogs = blogs.sortedByDescending { it.publishedDate }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen_root"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Magazine Brand Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .padding(top = 28.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Incredible India",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = AshokaNavy
                        )
                    )
                    Text(
                        text = "Explore culture, heritage, food and festivals",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onNavigateToNotifications,
                    modifier = Modifier.testTag("notification_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (notifications.isNotEmpty()) {
                                Badge { Text(notifications.size.toString()) }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = AshokaNavy,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        // 2. Search Quick-Jump Bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .clickable { onNavigateToSearch() }
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .testTag("home_search_bar_trigger")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Search festivals, destinations, recipes...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // 3. Bento Header Widget Row (Dynamic IST Info / Travel Stats)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // IST Clock & Namaste Bento Card (Left)
                Card(
                    modifier = Modifier
                        .weight(1.1f)
                        .height(82.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, if (isSystemInDarkTheme()) BentoDarkBorderColor else BentoBorderColor),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(AshokaNavy.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🇮🇳", fontSize = 18.sp)
                        }
                        Column {
                            Text(
                                text = "Namaste",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Incredible India",
                                style = MaterialTheme.typography.titleSmall,
                                color = AshokaNavy,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Travel Quote / Stats Bento Card (Right)
                Card(
                    modifier = Modifier
                        .weight(0.9f)
                        .height(82.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, if (isSystemInDarkTheme()) BentoDarkBorderColor else BentoBorderColor),
                    colors = CardDefaults.cardColors(containerColor = SaffronPrimary.copy(alpha = 0.12f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(SaffronPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Explore,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Explore",
                                style = MaterialTheme.typography.labelSmall,
                                color = SaffronPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "28 States & UTs",
                                style = MaterialTheme.typography.titleSmall,
                                color = AshokaNavy,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // 4. Featured Hero Bento Card
        if (isBlogsLoading) {
            item {
                val brush = shimmerBrush()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .testTag("home_skeleton_loader")
                ) {
                    // 1. Shimmer Header
                    Text(
                        text = "Featured Journey",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AshokaNavy,
                        modifier = Modifier.padding(start = 24.dp, bottom = 12.dp)
                    )

                    // 2. Large Shimmer Bento Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .height(260.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(brush)
                            .testTag("skeleton_hero_card")
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 3. Shimmer Row of Remaining Featured Items
                    Text(
                        text = "Curated Discoveries",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AshokaNavy,
                        modifier = Modifier.padding(start = 24.dp, bottom = 12.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(3) { index ->
                            Box(
                                modifier = Modifier
                                    .width(220.dp)
                                    .height(110.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(brush)
                                    .testTag("skeleton_curated_card_$index")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 4. Shimmer Asymmetric Bento Row
                    Text(
                        text = "Explore Categories",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AshokaNavy,
                        modifier = Modifier.padding(start = 24.dp, bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1.1f)
                                .height(240.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(brush)
                                .testTag("skeleton_asymmetric_left")
                        )
                        Box(
                            modifier = Modifier
                                .weight(0.9f)
                                .height(240.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(brush)
                                .testTag("skeleton_asymmetric_right")
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 5. Shimmer Trending Now Row
                    Text(
                        text = "Trending Now",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AshokaNavy,
                        modifier = Modifier.padding(start = 24.dp, bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(180.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(brush)
                                .testTag("skeleton_trending_left")
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(180.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(brush)
                                .testTag("skeleton_trending_right")
                        )
                    }
                }
            }
        } else {
            if (featuredBlogs.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Text(
                        text = "Featured Journey",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AshokaNavy,
                        modifier = Modifier.padding(start = 24.dp, bottom = 12.dp)
                    )

                    val topFeatured = featuredBlogs.first()
                    // Primary Hero Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .height(260.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable { onNavigateToBlog(topFeatured.id) }
                            .testTag("featured_blog_card_${topFeatured.id}")
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(topFeatured.featuredImageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Elegant Gradient Overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.2f),
                                            Color.Black.copy(alpha = 0.85f)
                                        )
                                    )
                                )
                        )

                        // Floating Badges
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(SaffronPrimary, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = topFeatured.category,
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = topFeatured.stateName,
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Bottom Text details
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(20.dp)
                        ) {
                            Text(
                                text = "${topFeatured.readTimeMin} min read • By ${topFeatured.authorName}",
                                color = Color.White.copy(alpha = 0.85f),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = topFeatured.title,
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Side-scroll remaining featured items
                    if (featuredBlogs.size > 1) {
                        Spacer(modifier = Modifier.height(14.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(featuredBlogs.drop(1)) { blog ->
                                Card(
                                    modifier = Modifier
                                        .width(220.dp)
                                        .height(110.dp)
                                        .clickable { onNavigateToBlog(blog.id) },
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, if (isSystemInDarkTheme()) BentoDarkBorderColor else BentoBorderColor),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                ) {
                                    Row(modifier = Modifier.fillMaxSize()) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(blog.featuredImageUrl)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .width(80.dp)
                                                .fillMaxHeight(),
                                            contentScale = ContentScale.Crop
                                        )
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(10.dp),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = blog.title,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 3,
                                                overflow = TextOverflow.Ellipsis,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = blog.category,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = IndiaGreen,
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
        }

        // 5. Asymmetric Bento Grid Row (States & Categories)
        item {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Text(
                    text = "Discover India",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AshokaNavy,
                    modifier = Modifier.padding(start = 24.dp, bottom = 12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left Column Bento: States (Weight 1.1)
                    Card(
                        modifier = Modifier
                            .weight(1.1f)
                            .height(240.dp),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, if (isSystemInDarkTheme()) BentoDarkBorderColor else BentoBorderColor),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "States",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AshokaNavy
                                )
                                Text(
                                    text = "All",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = SaffronPrimary,
                                    modifier = Modifier.clickable { onNavigateToStates() }
                                )
                            }

                            if (states.isNotEmpty()) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f).padding(top = 8.dp)
                                ) {
                                    states.take(2).forEach { state ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                                .clip(RoundedCornerShape(14.dp))
                                                .clickable {
                                                    viewModel.selectState(state.name)
                                                    onNavigateToSearch()
                                                }
                                        ) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(state.imageUrl)
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = state.name,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(
                                                        Brush.verticalGradient(
                                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f))
                                                        )
                                                    )
                                            )
                                            Text(
                                                text = state.name,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier
                                                    .align(Alignment.BottomStart)
                                                    .padding(8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Right Column Bento: Quick Categories Grid (Weight 0.9)
                    Card(
                        modifier = Modifier
                            .weight(0.9f)
                            .height(240.dp),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, if (isSystemInDarkTheme()) BentoDarkBorderColor else BentoBorderColor),
                        colors = CardDefaults.cardColors(containerColor = WarmSandCard),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Categories",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = AshokaNavy
                            )

                            if (categories.isNotEmpty()) {
                                Column(
                                    modifier = Modifier.weight(1f).padding(top = 10.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    categories.take(4).forEach { category ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                                .background(Color.White, RoundedCornerShape(12.dp))
                                                .border(BorderStroke(1.dp, if (isSystemInDarkTheme()) BentoDarkBorderColor else BentoBorderColor), RoundedCornerShape(12.dp))
                                                .clickable {
                                                    viewModel.selectCategory(category.name)
                                                    onNavigateToSearch()
                                                }
                                                .padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = getCategoryIcon(category.iconName),
                                                contentDescription = null,
                                                tint = SaffronPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = category.name,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = EarthyCharcoal,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. Trending Stories Bento Box
        if (trendingBlogs.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = SaffronPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Trending Now",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AshokaNavy
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        trendingBlogs.take(2).forEach { blog ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(180.dp)
                                    .clickable { onNavigateToBlog(blog.id) },
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, if (isSystemInDarkTheme()) BentoDarkBorderColor else BentoBorderColor),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(blog.featuredImageUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )

                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                                )
                                            )
                                    )

                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(SaffronPrimary, RoundedCornerShape(8.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "${blog.viewCount} views",
                                                color = Color.White,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = blog.title,
                                            color = Color.White,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 7. Editor's Picks (High Elegant Cards)
        if (editorsPicks.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Text(
                        text = "Editor's Pick",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 24.dp, bottom = 12.dp),
                        color = AshokaNavy
                    )

                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        editorsPicks.forEach { blog ->
                            EditorsPickCard(blog = blog, onClick = { onNavigateToBlog(blog.id) })
                        }
                    }
                }
            }
        }

        // 8. Recent Stories (Beautiful Magazine Feed)
        if (recentBlogs.isNotEmpty()) {
            item {
                Text(
                    text = "Recently Published",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 24.dp, top = 16.dp, end = 24.dp, bottom = 12.dp),
                    color = AshokaNavy
                )
            }

            items(recentBlogs) { blog ->
                RecentBlogRowItem(blog = blog, onClick = { onNavigateToBlog(blog.id) })
            }
        }

        // 9. Newsletter Subscription Footer Card
        item {
            NewsletterFooter(viewModel = viewModel)
        }
        }
    }
}

// --- COMPOSE SUB-COMPONENTS ---

@Composable
fun FeaturedBlogCard(
    blog: BlogEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(340.dp)
            .clickable { onClick() }
            .testTag("featured_blog_card_${blog.id}"),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, if (isSystemInDarkTheme()) BentoDarkBorderColor else BentoBorderColor),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(blog.featuredImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Dynamic gradients to assure text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.1f),
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Floating Saffron category badge at top left
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .background(SaffronPrimary, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = blog.category,
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            // Bottom details
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = blog.stateName,
                        color = Color(0xFFFFF1E6),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "•",
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "${blog.readTimeMin} min read",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = blog.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun StateCard(
    state: StateEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(110.dp)
            .clickable { onClick() }
            .testTag("state_card_${state.name}"),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if (isSystemInDarkTheme()) BentoDarkBorderColor else BentoBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(state.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = state.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                        )
                    )
            )

            Text(
                text = state.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun CategoryChip(
    category: CategoryEntity,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(16.dp)
            )
            .border(
                BorderStroke(1.dp, if (isSystemInDarkTheme()) BentoDarkBorderColor else BentoBorderColor),
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("category_chip_${category.name}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = getCategoryIcon(category.iconName),
            contentDescription = null,
            tint = SaffronPrimary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TrendingBlogCard(
    blog: BlogEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(230.dp)
            .height(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if (isSystemInDarkTheme()) BentoDarkBorderColor else BentoBorderColor),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(blog.featuredImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = "${blog.viewCount} reads",
                    color = SaffronPrimary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = blog.title,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun EditorsPickCard(
    blog: BlogEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if (isSystemInDarkTheme()) BentoDarkBorderColor else BentoBorderColor),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(blog.featuredImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "${blog.category} • ${blog.stateName}",
                        color = IndiaGreen,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = blog.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = blog.authorName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${blog.readTimeMin} min",
                        style = MaterialTheme.typography.labelSmall,
                        color = SaffronPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun RecentBlogRowItem(
    blog: BlogEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if (isSystemInDarkTheme()) BentoDarkBorderColor else BentoBorderColor),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(blog.featuredImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${blog.category} • ${blog.stateName}",
                    color = SaffronPrimary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = blog.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = blog.authorName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(text = "•", color = Color.Gray)
                    Text(
                        text = "${blog.readTimeMin} min read",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// Global helper to map DB string representation to M3 vector icons
fun getCategoryIcon(name: String): ImageVector {
    return when (name.lowercase()) {
        "celebration", "festivals" -> Icons.Default.Celebration
        "explore", "hidden gems" -> Icons.Default.Explore
        "museum", "culture & heritage" -> Icons.Default.Museum
        "restaurant", "food & cuisine" -> Icons.Default.Restaurant
        "terrain", "adventure" -> Icons.Default.Terrain
        "pets", "wildlife" -> Icons.Default.Pets
        "beach_access", "beaches" -> Icons.Default.BeachAccess
        "filter_hdr", "mountains" -> Icons.Default.FilterHdr
        "spa", "spiritual places" -> Icons.Default.Spa
        "account_balance", "historical monuments" -> Icons.Default.AccountBalance
        "directions_car", "road trips" -> Icons.Default.DirectionsCar
        "shopping_bag", "shopping" -> Icons.Default.ShoppingBag
        "groups", "local experiences" -> Icons.Default.Groups
        else -> Icons.Default.Explore
    }
}

@Composable
fun NewsletterFooter(
    viewModel: BlogViewModel
) {
    val context = LocalContext.current
    val isSubscribed by viewModel.newsletterSubscribed.collectAsState()
    val isSubscribing by viewModel.isSubscribing.collectAsState()
    var emailInput by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .testTag("newsletter_footer_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        border = BorderStroke(1.dp, BentoBorderColor)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = SaffronPrimary,
                modifier = Modifier.size(40.dp)
            )

            Text(
                text = "Subscribe to Newsletter",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AshokaNavy,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Receive weekly highlights of Indian travel stories, heritage discoveries, and hidden gems directly in your inbox.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            if (isSubscribed) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(IndiaGreen.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = IndiaGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "You are successfully subscribed! Welcome to the weekly highlight circle.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = IndiaGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    placeholder = { Text("Enter your email address") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = Color.Gray)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("newsletter_email_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SaffronPrimary,
                        unfocusedBorderColor = BentoBorderColor
                    )
                )

                Button(
                    onClick = {
                        if (emailInput.isBlank()) {
                            Toast.makeText(context, "Please enter an email address", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.subscribeToNewsletter(
                                email = emailInput,
                                onSuccess = {
                                    emailInput = ""
                                    Toast.makeText(context, "Subscription successful!", Toast.LENGTH_SHORT).show()
                                },
                                onError = { error ->
                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("newsletter_subscribe_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                    enabled = !isSubscribing
                ) {
                    if (isSubscribing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Subscribe Now",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun shimmerBrush(
    showShimmer: Boolean = true,
    targetValue: Float = 1000f
): Brush {
    return if (showShimmer) {
        val transition = rememberInfiniteTransition(label = "shimmer")
        val translateAnimation by transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1200,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmer_anim"
        )
        
        Brush.linearGradient(
            colors = listOf(
                Color.LightGray.copy(alpha = 0.6f),
                Color.LightGray.copy(alpha = 0.2f),
                Color.LightGray.copy(alpha = 0.6f),
            ),
            start = Offset.Zero,
            end = Offset(x = translateAnimation, y = translateAnimation)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
}
