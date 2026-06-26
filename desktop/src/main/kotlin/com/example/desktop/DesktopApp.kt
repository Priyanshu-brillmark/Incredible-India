package com.example.desktop

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.desktop.screens.BlogDetailScreen
import com.example.desktop.screens.BookmarksScreen
import com.example.desktop.screens.HomeScreen
import com.example.desktop.screens.SearchScreen
import com.example.desktop.theme.AshokaNavy
import com.example.desktop.theme.DesktopTheme
import com.example.desktop.theme.SaffronPrimary
import com.example.shared.data.InMemoryBlogRepository

private sealed class DesktopRoute {
    data object Home : DesktopRoute()
    data object Search : DesktopRoute()
    data object Saved : DesktopRoute()
    data class Detail(val blogId: Int) : DesktopRoute()
}

@Composable
fun DesktopApp() {
    val repository = remember { InMemoryBlogRepository() }
    var route by remember { mutableStateOf<DesktopRoute>(DesktopRoute.Home) }

    DesktopTheme {
        Scaffold(
            bottomBar = {
                if (route !is DesktopRoute.Detail) {
                    NavigationBar(
                        containerColor = AshokaNavy,
                        contentColor = Color.White
                    ) {
                        NavigationBarItem(
                            selected = route is DesktopRoute.Home,
                            onClick = { route = DesktopRoute.Home },
                            icon = { Icon(Icons.Default.Explore, contentDescription = "Discover") },
                            label = { Text("Discover") },
                            colors = navColors(route is DesktopRoute.Home)
                        )
                        NavigationBarItem(
                            selected = route is DesktopRoute.Search,
                            onClick = { route = DesktopRoute.Search },
                            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                            label = { Text("Search") },
                            colors = navColors(route is DesktopRoute.Search)
                        )
                        NavigationBarItem(
                            selected = route is DesktopRoute.Saved,
                            onClick = { route = DesktopRoute.Saved },
                            icon = { Icon(Icons.Default.Bookmark, contentDescription = "Saved") },
                            label = { Text("Saved") },
                            colors = navColors(route is DesktopRoute.Saved)
                        )
                    }
                }
            }
        ) { padding ->
            when (val current = route) {
                DesktopRoute.Home -> HomeScreen(
                    repository = repository,
                    modifier = Modifier.padding(padding),
                    onOpenBlog = { route = DesktopRoute.Detail(it) }
                )

                DesktopRoute.Search -> SearchScreen(
                    repository = repository,
                    modifier = Modifier.padding(padding),
                    onOpenBlog = { route = DesktopRoute.Detail(it) }
                )

                DesktopRoute.Saved -> BookmarksScreen(
                    repository = repository,
                    modifier = Modifier.padding(padding),
                    onOpenBlog = { route = DesktopRoute.Detail(it) },
                    onExplore = { route = DesktopRoute.Home }
                )

                is DesktopRoute.Detail -> BlogDetailScreen(
                    blogId = current.blogId,
                    repository = repository,
                    modifier = Modifier.padding(padding),
                    onBack = { route = DesktopRoute.Home },
                    onOpenBlog = { route = DesktopRoute.Detail(it) }
                )
            }
        }
    }
}

@Composable
private fun navColors(selected: Boolean) = NavigationBarItemDefaults.colors(
    selectedIconColor = SaffronPrimary,
    selectedTextColor = SaffronPrimary,
    indicatorColor = Color.White.copy(alpha = 0.15f),
    unselectedIconColor = Color.White.copy(alpha = 0.7f),
    unselectedTextColor = Color.White.copy(alpha = 0.7f)
)
