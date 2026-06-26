package com.example.desktop.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.desktop.theme.AshokaNavy
import com.example.shared.data.InMemoryBlogRepository
import com.example.shared.models.Blog

@Composable
fun SearchScreen(
    repository: InMemoryBlogRepository,
    modifier: Modifier = Modifier,
    onOpenBlog: (Int) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val results = remember(query) { repository.searchBlogs(query) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Search Stories",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = AshokaNavy
                )
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Search by title, category, state, or tag") },
                    singleLine = true
                )
            }
        }

        items(results) { blog ->
            SearchResultCard(blog = blog, onClick = { onOpenBlog(blog.id) })
        }
    }
}

@Composable
private fun SearchResultCard(blog: Blog, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(blog.title, fontWeight = FontWeight.Bold, color = AshokaNavy)
            Text(blog.subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${blog.category} · ${blog.stateName}", style = MaterialTheme.typography.labelMedium)
        }
    }
}
