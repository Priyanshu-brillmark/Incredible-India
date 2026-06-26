package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AshokaNavy
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.BentoBorderColor
import com.example.ui.theme.BentoDarkBorderColor
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import com.example.ui.viewmodel.BlogViewModel

@Composable
fun SearchScreen(
    viewModel: BlogViewModel,
    onNavigateToBlog: (Int) -> Unit
) {
    val query by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedState by viewModel.selectedState.collectAsState()
    
    val categories by viewModel.categories.collectAsState()
    val states by viewModel.states.collectAsState()
    val filteredBlogs by viewModel.filteredPublishedBlogs.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("search_screen_root"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Search Box Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 44.dp, bottom = 12.dp)
            ) {
                Text(
                    text = "Explore & Discover",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = AshokaNavy
                    )
                )
                Text(
                    text = "Find specific festivals, foods, states, or themes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search by keywords...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search icon",
                            tint = SaffronPrimary
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear search text",
                                    tint = Color.Gray
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input_field"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SaffronPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    singleLine = true
                )

                if (recentSearches.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Searches",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = AshokaNavy
                        )
                        Text(
                            text = "Clear All",
                            style = MaterialTheme.typography.labelSmall,
                            color = SaffronPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { viewModel.clearRecentSearches() }
                                .padding(4.dp)
                                .testTag("clear_recent_searches_btn")
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("recent_searches_row")
                    ) {
                        items(recentSearches) { term ->
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .clickable { 
                                        viewModel.setSearchQuery(term)
                                        viewModel.addRecentSearch(term)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = term,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove search term",
                                    tint = Color.Gray,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable { viewModel.removeRecentSearch(term) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Filter Category Row
        item {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "Category Filter",
                    style = MaterialTheme.typography.labelLarge,
                    color = AshokaNavy,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 6.dp),
                    fontWeight = FontWeight.Bold
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            text = "All",
                            isSelected = selectedCategory == null,
                            onClick = { viewModel.selectCategory(null) }
                        )
                    }
                    items(categories) { category ->
                        FilterChip(
                            text = category.name,
                            isSelected = selectedCategory == category.name,
                            onClick = { viewModel.selectCategory(category.name) }
                        )
                    }
                }
            }
        }

        // 3. Filter State Row
        item {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "State Filter",
                    style = MaterialTheme.typography.labelLarge,
                    color = AshokaNavy,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 6.dp),
                    fontWeight = FontWeight.Bold
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            text = "All States",
                            isSelected = selectedState == null,
                            onClick = { viewModel.selectState(null) }
                        )
                    }
                    items(states) { state ->
                        FilterChip(
                            text = state.name,
                            isSelected = selectedState == state.name,
                            onClick = { viewModel.selectState(state.name) }
                        )
                    }
                }
            }
        }

        // 4. Reset Filters bar if filters are active
        if (query.isNotEmpty() || selectedCategory != null || selectedState != null) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            tint = SaffronPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Active Filters applied",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = SaffronPrimary
                        )
                    }

                    Text(
                        text = "Clear All",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = AshokaNavy,
                        modifier = Modifier
                            .clickable { viewModel.clearFilters() }
                            .padding(4.dp)
                            .testTag("clear_filters_button")
                    )
                }
            }
        }

        // 5. Query Results
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Results (${filteredBlogs.size})",
                style = MaterialTheme.typography.titleMedium,
                color = AshokaNavy,
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 12.dp),
                fontWeight = FontWeight.Bold
            )
        }

        if (filteredBlogs.isEmpty()) {
            item {
                SearchEmptyState(
                    query = query,
                    onReset = { viewModel.clearFilters() }
                )
            }
        } else {
            items(filteredBlogs) { blog ->
                RecentBlogRowItem(
                    blog = blog,
                    onClick = {
                        if (query.isNotBlank()) {
                            viewModel.addRecentSearch(query)
                        }
                        onNavigateToBlog(blog.id)
                    }
                )
            }
        }
    }
}

@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) SaffronPrimary else MaterialTheme.colorScheme.surface
    val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = if (isSystemInDarkTheme()) BentoDarkBorderColor else BentoBorderColor

    Box(
        modifier = Modifier
            .clickable { onClick() }
            .background(bgColor, RoundedCornerShape(14.dp))
            .then(
                if (!isSelected) {
                    Modifier.border(BorderStroke(1.dp, borderColor), RoundedCornerShape(14.dp))
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("filter_chip_$text")
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun SearchEmptyState(
    query: String,
    onReset: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.TravelExplore,
                contentDescription = null,
                tint = SaffronPrimary,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No Journals Found",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AshokaNavy
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (query.isNotBlank()) {
                    "We couldn't find any travel stories matching \"$query\". Try checking spelling, changing filters, or search more general terms."
                } else {
                    "No articles match your selected state or category filter. Expand your search to discover amazing destinations."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onReset,
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Reset Filters")
            }
        }
    }
}
