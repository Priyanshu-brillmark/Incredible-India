package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.ui.theme.IndiaGreen
import com.example.ui.theme.SaffronPrimary
import com.example.ui.viewmodel.BlogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSuiteScreen(
    viewModel: BlogViewModel,
    onExitAdmin: () -> Unit
) {
    val context = LocalContext.current
    var subScreen by remember { mutableStateOf("dashboard") } // dashboard, blogs, categories, states, editor
    var editingBlog by remember { mutableStateOf<BlogEntity?>(null) } // null means Create mode

    val blogs by viewModel.allBlogs.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val states by viewModel.states.collectAsState()

    // Screen Title Mapping
    val screenTitle = when (subScreen) {
        "dashboard" -> "Admin Overview"
        "blogs" -> "Manage Blog Journals"
        "categories" -> "Manage Travel Categories"
        "states" -> "Manage Regional States"
        "editor" -> if (editingBlog == null) "Create Travel Journal" else "Modify Travel Journal"
        else -> "Admin Suite"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        screenTitle,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (subScreen == "dashboard") {
                                onExitAdmin()
                            } else {
                                subScreen = "dashboard"
                            }
                        },
                        modifier = Modifier.testTag("admin_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (subScreen == "dashboard") {
                        Button(
                            onClick = {
                                editingBlog = null
                                subScreen = "editor"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(end = 8.dp).testTag("quick_create_blog_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Blog", color = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AshokaNavy)
            )
        },
        modifier = Modifier.testTag("admin_suite_root")
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (subScreen) {
                "dashboard" -> AdminDashboardView(
                    blogs = blogs,
                    categories = categories,
                    states = states,
                    onNavigateTo = { subScreen = it }
                )
                "blogs" -> AdminBlogsListView(
                    blogs = blogs,
                    onEditBlog = { blog ->
                        editingBlog = blog
                        subScreen = "editor"
                    },
                    onTogglePublish = { blog ->
                        viewModel.saveBlog(
                            id = blog.id,
                            title = blog.title,
                            subtitle = blog.subtitle,
                            author = blog.authorName,
                            category = blog.category,
                            state = blog.stateName,
                            imageUrl = blog.featuredImageUrl,
                            content = blog.content,
                            tags = blog.tagsRaw,
                            gallery = blog.galleryRaw,
                            isPublished = !blog.isPublished, // Toggle
                            isFeatured = blog.isFeatured,
                            isTrending = blog.isTrending,
                            isEditorsPick = blog.isEditorsPick,
                            seoTitle = blog.seoTitle,
                            seoDescription = blog.seoDescription
                        ) {
                            Toast.makeText(context, "Published status updated!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onDeleteBlog = { blogId ->
                        viewModel.deleteBlog(blogId) {
                            Toast.makeText(context, "Blog successfully deleted", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
                "categories" -> AdminCategoriesView(
                    categories = categories,
                    onAddCategory = { name, icon ->
                        viewModel.addCategory(name, icon)
                        Toast.makeText(context, "Category Added!", Toast.LENGTH_SHORT).show()
                    },
                    onDeleteCategory = { id ->
                        viewModel.deleteCategory(id)
                        Toast.makeText(context, "Category Removed!", Toast.LENGTH_SHORT).show()
                    }
                )
                "states" -> AdminStatesView(
                    states = states,
                    onAddState = { name, desc, img ->
                        viewModel.addState(name, desc, img)
                        Toast.makeText(context, "State Registered!", Toast.LENGTH_SHORT).show()
                    },
                    onDeleteState = { id ->
                        viewModel.deleteState(id)
                        Toast.makeText(context, "State Removed!", Toast.LENGTH_SHORT).show()
                    }
                )
                "editor" -> AdminContentEditorView(
                    blog = editingBlog,
                    categories = categories,
                    states = states,
                    onSave = { id, title, subtitle, author, cat, st, img, content, tags, gal, pub, feat, trend, editPick, seoT, seoD ->
                        viewModel.saveBlog(
                            id, title, subtitle, author, cat, st, img, content, tags, gal, pub, feat, trend, editPick, seoT, seoD
                        ) { success ->
                            if (success) {
                                Toast.makeText(context, "Journal Saved successfully!", Toast.LENGTH_LONG).show()
                                subScreen = "dashboard"
                            } else {
                                Toast.makeText(context, "Error saving article, check fields", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    onCancel = { subScreen = "dashboard" }
                )
            }
        }
    }
}

// --- SUB-VIEW 1: DASHBOARD ---
@Composable
fun AdminDashboardView(
    blogs: List<BlogEntity>,
    categories: List<CategoryEntity>,
    states: List<StateEntity>,
    onNavigateTo: (String) -> Unit
) {
    val totalPublished = blogs.count { it.isPublished }
    val totalDrafts = blogs.count { !it.isPublished }
    val totalFeatured = blogs.count { it.isFeatured }
    val cumulativeViews = blogs.sumOf { it.viewCount }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Welcoming card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AshokaNavy),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = SaffronPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Welcome, Incredible Admin!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Manage and publish premium articles exploring India.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Analytics Title
        item {
            Text("Analytics Overview", style = MaterialTheme.typography.titleMedium, color = AshokaNavy, fontWeight = FontWeight.Bold)
        }

        // Analytics Grid of 4 Items
        item {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    AnalyticsCard(modifier = Modifier.weight(1f), number = totalPublished.toString(), label = "Published Blogs", color = IndiaGreen)
                    AnalyticsCard(modifier = Modifier.weight(1f), number = totalDrafts.toString(), label = "Drafts Saved", color = SaffronPrimary)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    AnalyticsCard(modifier = Modifier.weight(1f), number = totalFeatured.toString(), label = "Featured Stories", color = AshokaNavy)
                    AnalyticsCard(modifier = Modifier.weight(1f), number = cumulativeViews.toString(), label = "Cumulative Reads", color = Color(0xFFD35400))
                }
            }
        }

        // Section Managers
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text("Manage Modules", style = MaterialTheme.typography.titleMedium, color = AshokaNavy, fontWeight = FontWeight.Bold)
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ModuleNavigatorCard(
                    title = "Manage Blog Articles",
                    subtitle = "Create, edit, toggle drafts, or delete journals",
                    icon = Icons.Default.MenuBook,
                    tint = SaffronPrimary,
                    onClick = { onNavigateTo("blogs") }
                )

                ModuleNavigatorCard(
                    title = "Manage Categories",
                    subtitle = "Add and remove search categories and icon chips",
                    icon = Icons.Default.Category,
                    tint = IndiaGreen,
                    onClick = { onNavigateTo("categories") }
                )

                ModuleNavigatorCard(
                    title = "Manage Regional States",
                    subtitle = "Configure Indian states descriptions and hero pictures",
                    icon = Icons.Default.Map,
                    tint = AshokaNavy,
                    onClick = { onNavigateTo("states") }
                )
            }
        }
    }
}

@Composable
fun AnalyticsCard(
    modifier: Modifier = Modifier,
    number: String,
    label: String,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = number, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = color)
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ModuleNavigatorCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = tint)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AshokaNavy)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

// --- SUB-VIEW 2: BLOGS LIST ---
@Composable
fun AdminBlogsListView(
    blogs: List<BlogEntity>,
    onEditBlog: (BlogEntity) -> Unit,
    onTogglePublish: (BlogEntity) -> Unit,
    onDeleteBlog: (Int) -> Unit
) {
    if (blogs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No blogs registered in system.")
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(blogs) { blog ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(blog.featuredImageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = blog.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = AshokaNavy
                        )

                        Text(
                            text = "Author: ${blog.authorName} • ${blog.category}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Publish status badge
                            val badgeColor = if (blog.isPublished) IndiaGreen else SaffronPrimary
                            Text(
                                text = if (blog.isPublished) "Published" else "Draft",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .background(badgeColor, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )

                            if (blog.isFeatured) {
                                Text(
                                    text = "Featured",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .background(AshokaNavy, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // Action buttons
                    Row {
                        IconButton(onClick = { onTogglePublish(blog) }) {
                            Icon(
                                imageVector = if (blog.isPublished) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle publish",
                                tint = AshokaNavy
                            )
                        }
                        IconButton(onClick = { onEditBlog(blog) }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = IndiaGreen)
                        }
                        IconButton(onClick = { onDeleteBlog(blog.id) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

// --- SUB-VIEW 3: CATEGORIES ---
@Composable
fun AdminCategoriesView(
    categories: List<CategoryEntity>,
    onAddCategory: (String, String) -> Unit,
    onDeleteCategory: (Int) -> Unit
) {
    var newCatName by remember { mutableStateOf("") }
    var selectedIconName by remember { mutableStateOf("explore") }

    val iconOptions = listOf(
        "celebration", "explore", "museum", "restaurant", "terrain", "pets", "beach_access", "filter_hdr", "spa", "account_balance", "directions_car", "shopping_bag", "groups"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Quick adder Form
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Register New Category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AshokaNavy)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = newCatName,
                    onValueChange = { newCatName = it },
                    label = { Text("Category Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Select Vector Icon Tag", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(iconOptions) { opt ->
                        val isSel = opt == selectedIconName
                        Box(
                            modifier = Modifier
                                .clickable { selectedIconName = opt }
                                .background(
                                    if (isSel) SaffronPrimary else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = getCategoryIcon(opt),
                                contentDescription = null,
                                tint = if (isSel) Color.White else AshokaNavy
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (newCatName.isNotBlank()) {
                            onAddCategory(newCatName.trim(), selectedIconName)
                            newCatName = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                ) {
                    Text("Add Category To DB")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Active Categories", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AshokaNavy)
        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(categories) { cat ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = getCategoryIcon(cat.iconName), contentDescription = null, tint = SaffronPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(cat.name, fontWeight = FontWeight.Bold)
                    }

                    IconButton(onClick = { onDeleteCategory(cat.id) }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                }
            }
        }
    }
}

// --- SUB-VIEW 4: STATES ---
@Composable
fun AdminStatesView(
    states: List<StateEntity>,
    onAddState: (String, String, String) -> Unit,
    onDeleteState: (Int) -> Unit
) {
    var stateName by remember { mutableStateOf("") }
    var stateDesc by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Quick adder Form
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Register New State / UT", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AshokaNavy)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = stateName,
                    onValueChange = { stateName = it },
                    label = { Text("State Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndiaGreen)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = stateDesc,
                    onValueChange = { stateDesc = it },
                    label = { Text("Brief Description Slogan") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndiaGreen)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Unsplash Cover Photo URL") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndiaGreen)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (stateName.isNotBlank() && stateDesc.isNotBlank() && imageUrl.isNotBlank()) {
                            onAddState(stateName.trim(), stateDesc.trim(), imageUrl.trim())
                            stateName = ""
                            stateDesc = ""
                            imageUrl = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = IndiaGreen)
                ) {
                    Text("Register State In DB")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Configured States", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AshokaNavy)
        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            states.forEach { st ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(st.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(st.name, fontWeight = FontWeight.Bold, color = AshokaNavy)
                            Text(st.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }

                    IconButton(onClick = { onDeleteState(st.id) }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                }
            }
        }
    }
}

// --- SUB-VIEW 5: BLOG CONTENT WRITER EDITOR ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminContentEditorView(
    blog: BlogEntity?,
    categories: List<CategoryEntity>,
    states: List<StateEntity>,
    onSave: (
        id: Int, title: String, subtitle: String, author: String, category: String, state: String, imageUrl: String, content: String, tags: String, gallery: String, isPublished: Boolean, isFeatured: Boolean, isTrending: Boolean, isEditorsPick: Boolean, seoTitle: String, seoDescription: String
    ) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(blog?.title ?: "") }
    var subtitle by remember { mutableStateOf(blog?.subtitle ?: "") }
    var author by remember { mutableStateOf(blog?.authorName ?: "") }
    var category by remember { mutableStateOf(blog?.category ?: if (categories.isNotEmpty()) categories[0].name else "Festivals") }
    var stateName by remember { mutableStateOf(blog?.stateName ?: if (states.isNotEmpty()) states[0].name else "Rajasthan") }
    var imageUrl by remember { mutableStateOf(blog?.featuredImageUrl ?: "") }
    var contentText by remember { mutableStateOf(blog?.content ?: "") }
    var tagsRaw by remember { mutableStateOf(blog?.tagsRaw ?: "") }
    var galleryRaw by remember { mutableStateOf(blog?.galleryRaw ?: "") }
    var isPublished by remember { mutableStateOf(blog?.isPublished ?: true) }
    var isFeatured by remember { mutableStateOf(blog?.isFeatured ?: false) }
    var isTrending by remember { mutableStateOf(blog?.isTrending ?: false) }
    var isEditorsPick by remember { mutableStateOf(blog?.isEditorsPick ?: false) }
    var seoTitle by remember { mutableStateOf(blog?.seoTitle ?: "") }
    var seoDescription by remember { mutableStateOf(blog?.seoDescription ?: "") }

    var catExpanded by remember { mutableStateOf(false) }
    var stateExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Step 1: Primary Details",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = SaffronPrimary
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Journal Title") },
            modifier = Modifier.fillMaxWidth().testTag("blog_title_input"),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
        )

        OutlinedTextField(
            value = subtitle,
            onValueChange = { subtitle = it },
            label = { Text("Subtitle / Summary Slogan") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
        )

        OutlinedTextField(
            value = author,
            onValueChange = { author = it },
            label = { Text("Author Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
        )

        // Dropdowns for Category & State
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Category Dropdown
            Box(modifier = Modifier.weight(1f)) {
                ExposedDropdownMenuBox(
                    expanded = catExpanded,
                    onExpandedChange = { catExpanded = !catExpanded }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = category,
                        onValueChange = {},
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                        modifier = Modifier.menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
                    )
                    ExposedDropdownMenu(
                        expanded = catExpanded,
                        onDismissRequest = { catExpanded = false }
                    ) {
                        categories.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption.name) },
                                onClick = {
                                    category = selectionOption.name
                                    catExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // State Dropdown
            Box(modifier = Modifier.weight(1f)) {
                ExposedDropdownMenuBox(
                    expanded = stateExpanded,
                    onExpandedChange = { stateExpanded = !stateExpanded }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = stateName,
                        onValueChange = {},
                        label = { Text("State") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stateExpanded) },
                        modifier = Modifier.menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
                    )
                    ExposedDropdownMenu(
                        expanded = stateExpanded,
                        onDismissRequest = { stateExpanded = false }
                    ) {
                        states.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption.name) },
                                onClick = {
                                    stateName = selectionOption.name
                                    stateExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        OutlinedTextField(
            value = imageUrl,
            onValueChange = { imageUrl = it },
            label = { Text("Featured Cover Photo URL") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
        )

        Divider(color = Color.LightGray.copy(alpha = 0.5f))

        Text(
            text = "Step 2: Rich Article Text Editor",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = IndiaGreen
        )

        // WYSIWYG Content Area
        OutlinedTextField(
            value = contentText,
            onValueChange = { contentText = it },
            label = { Text("Write Article Body... (use ## or ### for headers)") },
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndiaGreen),
            maxLines = 100
        )

        OutlinedTextField(
            value = tagsRaw,
            onValueChange = { tagsRaw = it },
            label = { Text("Tags (Semicolon separated, e.g. Goa;Beaches;Food)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndiaGreen)
        )

        OutlinedTextField(
            value = galleryRaw,
            onValueChange = { galleryRaw = it },
            label = { Text("Gallery URLs (Semicolon separated)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndiaGreen)
        )

        Divider(color = Color.LightGray.copy(alpha = 0.5f))

        Text(
            text = "Step 3: SEO Optimization & Flags",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AshokaNavy
        )

        OutlinedTextField(
            value = seoTitle,
            onValueChange = { seoTitle = it },
            label = { Text("SEO Meta Title") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AshokaNavy)
        )

        OutlinedTextField(
            value = seoDescription,
            onValueChange = { seoDescription = it },
            label = { Text("SEO Meta Description") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AshokaNavy)
        )

        // Switched Flags Grid
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                EditorSwitchRow(label = "Publish Instantly", checked = isPublished, onCheckedChange = { isPublished = it })
                EditorSwitchRow(label = "Mark as Featured", checked = isFeatured, onCheckedChange = { isFeatured = it })
                EditorSwitchRow(label = "Mark as Trending", checked = isTrending, onCheckedChange = { isTrending = it })
                EditorSwitchRow(label = "Mark as Editor's Pick", checked = isEditorsPick, onCheckedChange = { isEditorsPick = it })
            }
        }

        // Action controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray.copy(alpha = 0.2f), contentColor = Color.DarkGray)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    if (title.isBlank() || author.isBlank() || imageUrl.isBlank() || contentText.isBlank()) {
                        return@Button
                    }
                    onSave(
                        blog?.id ?: 0,
                        title.trim(),
                        subtitle.trim(),
                        author.trim(),
                        category,
                        stateName,
                        imageUrl.trim(),
                        contentText.trim(),
                        tagsRaw.trim(),
                        galleryRaw.trim(),
                        isPublished,
                        isFeatured,
                        isTrending,
                        isEditorsPick,
                        seoTitle.trim(),
                        seoDescription.trim()
                    )
                },
                modifier = Modifier.weight(1.5f).testTag("save_blog_button"),
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save & Commit", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun EditorSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyLarge)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = SaffronPrimary,
                checkedTrackColor = SaffronPrimary.copy(alpha = 0.4f)
            )
        )
    }
}
