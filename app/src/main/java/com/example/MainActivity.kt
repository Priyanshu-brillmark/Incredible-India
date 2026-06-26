package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.AdminSuiteScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BlogDetailScreen
import com.example.ui.screens.BookmarksScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NotificationScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.AshokaNavy
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SaffronPrimary
import com.example.ui.viewmodel.BlogViewModel
import com.example.ui.viewmodel.BlogViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Get repository reference from custom Application class
            val repository = (application as IncredibleIndiaApplication).repository
            val viewModel: BlogViewModel = viewModel(
                factory = BlogViewModelFactory(repository, application)
            )

            MyApplicationTheme {
                MainAppShell(viewModel)
            }
        }
    }
}

@Composable
fun MainAppShell(viewModel: BlogViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // List of routes where Bottom Navigation should be displayed
    val bottomNavRoutes = listOf("home", "search", "saved", "profile")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentRoute in bottomNavRoutes) {
                NavigationBar(
                    containerColor = AshokaNavy,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = currentRoute == "home",
                        onClick = {
                            if (currentRoute != "home") {
                                navController.navigate("home") {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(Icons.Default.Explore, contentDescription = "Discover") },
                        label = { Text("Discover") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SaffronPrimary,
                            selectedTextColor = SaffronPrimary,
                            indicatorColor = Color.White.copy(alpha = 0.15f),
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            unselectedTextColor = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("nav_item_home")
                    )

                    NavigationBarItem(
                        selected = currentRoute == "search",
                        onClick = {
                            if (currentRoute != "search") {
                                navController.navigate("search") {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        label = { Text("Search") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SaffronPrimary,
                            selectedTextColor = SaffronPrimary,
                            indicatorColor = Color.White.copy(alpha = 0.15f),
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            unselectedTextColor = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("nav_item_search")
                    )

                    NavigationBarItem(
                        selected = currentRoute == "saved",
                        onClick = {
                            if (currentRoute != "saved") {
                                navController.navigate("saved") {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(Icons.Default.Bookmark, contentDescription = "Saved") },
                        label = { Text("Saved") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SaffronPrimary,
                            selectedTextColor = SaffronPrimary,
                            indicatorColor = Color.White.copy(alpha = 0.15f),
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            unselectedTextColor = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("nav_item_saved")
                    )

                    NavigationBarItem(
                        selected = currentRoute == "profile",
                        onClick = {
                            if (currentRoute != "profile") {
                                navController.navigate("profile") {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SaffronPrimary,
                            selectedTextColor = SaffronPrimary,
                            indicatorColor = Color.White.copy(alpha = 0.15f),
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            unselectedTextColor = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("nav_item_profile")
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(innerPadding)
        ) {
            // Splash Route
            composable("splash") {
                SplashScreen(
                    onNavigateNext = {
                        navController.navigate("onboarding") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                )
            }

            // Onboarding Route
            composable("onboarding") {
                OnboardingScreen(
                    onFinish = {
                        navController.navigate("auth") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                )
            }

            // Authentication Route (with Traveler & Admin portals)
            composable("auth") {
                AuthScreen(
                    viewModel = viewModel,
                    onSuccess = { isAdmin ->
                        if (isAdmin) {
                            navController.navigate("admin") {
                                popUpTo("auth") { inclusive = true }
                            }
                        } else {
                            navController.navigate("home") {
                                popUpTo("auth") { inclusive = true }
                            }
                        }
                    }
                )
            }

            // Home / Discover Feed
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToBlog = { blogId ->
                        navController.navigate("detail/$blogId")
                    },
                    onNavigateToSearch = {
                        navController.navigate("search")
                    },
                    onNavigateToNotifications = {
                        navController.navigate("notifications")
                    },
                    onNavigateToCategories = {
                        navController.navigate("search")
                    },
                    onNavigateToStates = {
                        navController.navigate("search")
                    }
                )
            }

            // Search Screen
            composable("search") {
                SearchScreen(
                    viewModel = viewModel,
                    onNavigateToBlog = { blogId ->
                        navController.navigate("detail/$blogId")
                    }
                )
            }

            // Saved / Bookmarks Screen
            composable("saved") {
                BookmarksScreen(
                    viewModel = viewModel,
                    onNavigateToBlog = { blogId ->
                        navController.navigate("detail/$blogId")
                    },
                    onExploreBlogs = {
                        navController.navigate("home")
                    }
                )
            }

            // Profile & Settings
            composable("profile") {
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToAdminDashboard = {
                        navController.navigate("admin")
                    },
                    onSignOut = {
                        viewModel.logout()
                        navController.navigate("auth") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToSaved = {
                        navController.navigate("saved")
                    },
                    onNavigateToNotifications = {
                        navController.navigate("notifications")
                    },
                    onNavigateToBlog = { blogId ->
                        navController.navigate("detail/$blogId")
                    }
                )
            }

            // Notifications Bulletin Board
            composable("notifications") {
                NotificationScreen(
                    viewModel = viewModel,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Blog Detail Viewer
            composable(
                route = "detail/{blogId}",
                arguments = listOf(navArgument("blogId") { type = NavType.IntType })
            ) { backStackEntry ->
                val blogId = backStackEntry.arguments?.getInt("blogId") ?: 0
                BlogDetailScreen(
                    blogId = blogId,
                    viewModel = viewModel,
                    onBack = {
                        navController.popBackStack()
                    },
                    onNavigateToBlog = { targetBlogId ->
                        navController.navigate("detail/$targetBlogId")
                    }
                )
            }

            // Admin Dashboard Suite (all sub-admin flows managed internally for speed)
            composable("admin") {
                AdminSuiteScreen(
                    viewModel = viewModel,
                    onExitAdmin = {
                        navController.navigate("profile") {
                            popUpTo("admin") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
