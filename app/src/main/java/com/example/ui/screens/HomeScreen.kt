package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.BackHandler
import com.example.app.viewmodel.AartiViewModel
import com.example.data.AartiEntity
import com.example.ui.components.AdMobBanner

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AartiViewModel,
    onAartiClick: (Int) -> Unit,
    onNavigateToBhajans: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToReminders: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val aartis by viewModel.allAartis.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val selectedDeity by viewModel.selectedDeity.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLanguageSelected by viewModel.isLanguageSelected.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var step by remember(isLanguageSelected) { mutableStateOf(if (isLanguageSelected) "DEITY" else "LANG") }
    var showLanguageDialog by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Hardware back button behavior:
    // 1. If searching, clear search query first.
    // 2. If viewing aarti/bhajan list for a deity, return to Deity grid.
    // 3. If category filter is active, reset to ALL.
    // 4. If at Deity grid, BackHandler is disabled, allowing standard Android exit/minimize.
    BackHandler(enabled = searchQuery.isNotBlank() || step == "AARTI" || selectedCategory != "ALL") {
        if (searchQuery.isNotBlank()) {
            viewModel.setSearchQuery("")
        } else if (step == "AARTI") {
            step = "DEITY"
        } else if (selectedCategory != "ALL") {
            viewModel.setSelectedCategory("ALL")
        }
    }

    val deities = listOf(
        Triple("Ganesha", "भगवान गणेश", "श्री गणेश"),
        Triple("Shiva", "भगवान शिव", "भगवान शिव"),
        Triple("Vishnu", "भगवान विष्णु", "श्री विष्णू"),
        Triple("Hanuman", "संकटमोचन हनुमान", "संकटमोचन हनुमान"),
        Triple("Durga", "माता दुर्गा", "माता दुर्गा"),
        Triple("KhatuShyam", "खाटू श्याम बाबा", "खाटू श्याम बाबा"),
        Triple("Shani", "शनि देव", "शनि देव"),
        Triple("Saraswati", "माता सरस्वती", "माता सरस्वती")
    )

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = {
                Text(
                    text = when (currentLang) {
                        "hi" -> "भाषा चुनें"
                        "mr" -> "भाषा निवडा"
                        else -> "Select Language"
                    },
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "mr" to "मराठी (Marathi)",
                        "hi" to "हिंदी (Hindi)",
                        "en" to "English"
                    ).forEach { (code, name) ->
                        val isSelected = currentLang == code
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.setLanguage(code)
                                    showLanguageDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = name,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(
                        when (currentLang) {
                            "hi" -> "बंद करें"
                            "mr" -> "बंद करा"
                            else -> "Close"
                        }
                    )
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(24.dp))
                // Drawer Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Mantramaya Aarti & Bhajans",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "Room DB Offline Cached",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Menu Items
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Language, contentDescription = null) },
                    label = { Text(when(currentLang) { "hi" -> "भाषा बदलें (Change Language)"; "mr" -> "भाषा बदला (Change Language)"; else -> "Change Language" }) },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        showLanguageDialog = true
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                    label = { Text(when(currentLang) { "hi" -> "प्ले स्टोर पर रेटिंग दें"; "mr" -> "प्ले स्टोअरवर रेटिंग द्या"; else -> "Rate on Play Store" }) },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}"))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Fallback
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    label = { Text(when(currentLang) { "hi" -> "पसंद (Favorites)"; "mr" -> "आवडते (Favorites)"; else -> "Favorites" }) },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        onNavigateToFavorites()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                    label = { Text(when(currentLang) { "hi" -> "रिमाइंडर (Reminders)"; "mr" -> "रिमाइंडर (Reminders)"; else -> "Reminders" }) },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        onNavigateToReminders()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text(when(currentLang) { "hi" -> "सेटिंग्स (Settings)"; "mr" -> "सेटिंग्स (Settings)"; else -> "Settings" }) },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        onNavigateToSettings()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
                topBar = {
                    TopAppBar(
                        navigationIcon = {
                            if (step == "AARTI") {
                                IconButton(onClick = {
                                    viewModel.setSearchQuery("")
                                    step = "DEITY"
                                }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Back to Deities")
                                }
                            } else {
                                IconButton(onClick = {
                                    coroutineScope.launch { drawerState.open() }
                                }) {
                                    Icon(Icons.Default.Menu, contentDescription = "Open Menu")
                                }
                            }
                        },
                        title = {
                            Text(
                                text = when {
                                    selectedCategory == "Bhajan" -> when (currentLang) {
                                        "hi" -> "मंत्रमया भजन संग्रह"
                                        "mr" -> "मंत्रमया भजन संग्रह"
                                        else -> "Mantramaya Bhajan Sangrah"
                                    }
                                    else -> when (currentLang) {
                                        "hi" -> "मंत्रमया आरती, चालीसा व भजन"
                                        "mr" -> "मंत्रमया आरती, चालिसा व भजने"
                                        else -> "Mantramaya Aarti & Bhajans"
                                    }
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        actions = {
                            IconButton(onClick = onNavigateToFavorites) {
                                Icon(Icons.Default.Favorite, contentDescription = "Favorites")
                            }
                            IconButton(onClick = onNavigateToReminders) {
                                Icon(Icons.Default.Notifications, contentDescription = "Reminders")
                            }
                        }
                    )
                }
            ) { paddingVals ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingVals)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (step) {
                // ==================== STEP 1: LANGUAGE SELECTION ====================
                "LANG" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SelfImprovement,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "कृपया अपनी भाषा चुनें\nकृपया आपली भाषा निवडा\nSelect Your Preferred Language",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(32.dp))

                        // Marathi Button
                        Button(
                            onClick = {
                                viewModel.setLanguage("mr")
                                step = "DEITY"
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("मराठी (Marathi)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Hindi Button
                        Button(
                            onClick = {
                                viewModel.setLanguage("hi")
                                step = "DEITY"
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("हिंदी (Hindi)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // English Button
                        OutlinedButton(
                            onClick = {
                                viewModel.setLanguage("en")
                                step = "DEITY"
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("English", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // ==================== STEP 2: DEITY SELECTION ====================
                "DEITY" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when (currentLang) {
                                    "hi" -> if (selectedCategory == "Bhajan") "भजन संग्रह: अपने आराध्य देव चुनें" else "अपने आराध्य देव चुनें"
                                    "mr" -> if (selectedCategory == "Bhajan") "भजन संग्रह: आपले आराध्य दैवत निवडा" else "आपले आराध्य दैवत निवडा"
                                    else -> if (selectedCategory == "Bhajan") "Bhajans: Choose Deity" else "Choose Your Deity"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            TextButton(onClick = { showLanguageDialog = true }) {
                                Text(
                                    text = when (currentLang) {
                                        "hi" -> "भाषा बदलें"
                                        "mr" -> "भाषा बदला"
                                        else -> "Change Language"
                                    },
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Search Bar at the top of the Home screen
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    when (currentLang) {
                                        "hi" -> "आरती, चालीसा या भजन खोजें..."
                                        "mr" -> "आरती, चालिसा किंवा भजन शोधा..."
                                        else -> "Search Aartis, Chalisas, Bhajans..."
                                    }
                                )
                            },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            trailingIcon = {
                                if (searchQuery.isNotBlank()) {
                                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                                    }
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (searchQuery.isNotBlank()) {
                            val searchResults = aartis.filter {
                                it.titleEnglish.contains(searchQuery, ignoreCase = true) ||
                                        it.titleHindi.contains(searchQuery, ignoreCase = true) ||
                                        it.titleMarathi.contains(searchQuery, ignoreCase = true) ||
                                        it.deity.contains(searchQuery, ignoreCase = true) ||
                                        it.category.contains(searchQuery, ignoreCase = true)
                            }

                            if (searchResults.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.SearchOff,
                                            contentDescription = null,
                                            modifier = Modifier.size(64.dp),
                                            tint = MaterialTheme.colorScheme.outline
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = when (currentLang) {
                                                "hi" -> "कोई परिणाम नहीं मिला"
                                                "mr" -> "कोणतेही निकाल सापडले नाहीत"
                                                else -> "No results found"
                                            },
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(searchResults) { item ->
                                        val title = when (currentLang) {
                                            "hi" -> item.titleHindi.ifBlank { item.titleEnglish }
                                            "mr" -> item.titleMarathi.ifBlank { item.titleEnglish }
                                            else -> item.titleEnglish
                                        }

                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { onAartiClick(item.id) },
                                            shape = RoundedCornerShape(16.dp),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(14.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(46.dp)
                                                        .clip(CircleShape)
                                                        .background(
                                                            when (item.category) {
                                                                "Bhajan" -> MaterialTheme.colorScheme.primaryContainer
                                                                "Chalisa" -> MaterialTheme.colorScheme.tertiaryContainer
                                                                else -> MaterialTheme.colorScheme.secondaryContainer
                                                            }
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = when (item.category) {
                                                            "Bhajan" -> Icons.Default.MusicNote
                                                            "Chalisa" -> Icons.Default.MenuBook
                                                            else -> Icons.Default.SelfImprovement
                                                        },
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }

                                                Spacer(modifier = Modifier.width(14.dp))

                                                Column(modifier = Modifier.weight(1f)) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        Surface(
                                                            shape = RoundedCornerShape(6.dp),
                                                            color = MaterialTheme.colorScheme.secondaryContainer
                                                        ) {
                                                            Text(
                                                                text = "${item.deity} • ${item.category}",
                                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                                fontSize = 10.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                                            )
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = title,
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        fontWeight = FontWeight.Bold,
                                                        maxLines = 2,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }

                                                IconButton(
                                                    onClick = { viewModel.toggleFavorite(item) }
                                                ) {
                                                    Icon(
                                                        imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                        contentDescription = "Favorite",
                                                        tint = if (item.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Dedicated Bhajan Hub Banner Card
                        Card(
                            onClick = onNavigateToBhajans,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = when (currentLang) {
                                            "hi" -> "🎵 दिव्य भजन संग्रह (Bhajan Section)"
                                            "mr" -> "🎵 दिव्य भजन संग्रह (भजने)"
                                            else -> "🎵 Divine Bhajan Section"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = when (currentLang) {
                                            "hi" -> "प्रत्येक देवता के लिए विशेष भजन सुनें व पढ़ें"
                                            "mr" -> "प्रत्येक दैवताची विशेष भजने वाचा व अनुभवा"
                                            else -> "Explore special bhajans for each deity"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Category Filter Chips (All, Aarti, Chalisa, Bhajan)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val categories = listOf(
                                "ALL" to when (currentLang) { "hi" -> "सभी"; "mr" -> "सर्व"; else -> "All" },
                                "Aarti" to when (currentLang) { "hi" -> "आरती"; "mr" -> "आरती"; else -> "Aarti" },
                                "Chalisa" to when (currentLang) { "hi" -> "चालीसा"; "mr" -> "चालिसा"; else -> "Chalisa" },
                                "Bhajan" to when (currentLang) { "hi" -> "भजन"; "mr" -> "भजन"; else -> "Bhajan" }
                            )

                            categories.forEach { (catKey, label) ->
                                val isCatSelected = selectedCategory == catKey
                                FilterChip(
                                    selected = isCatSelected,
                                    onClick = { viewModel.setSelectedCategory(catKey) },
                                    label = {
                                        Text(
                                            text = label,
                                            fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 13.sp
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(deities) { (deityKey, hindiName, marathiName) ->
                                val displayName = when (currentLang) {
                                    "hi" -> hindiName
                                    "mr" -> marathiName
                                    else -> deityKey
                                }
                                val count = aartis.count {
                                    it.deity.equals(deityKey, ignoreCase = true) &&
                                            (selectedCategory == "ALL" || it.category.equals(selectedCategory, ignoreCase = true))
                                }
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(118.dp)
                                        .clickable {
                                            viewModel.setSelectedDeity(deityKey)
                                            step = "AARTI"
                                        },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(10.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primaryContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (selectedCategory == "Bhajan") Icons.Default.MusicNote else Icons.Default.SelfImprovement,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = displayName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = when (selectedCategory) {
                                                "Bhajan" -> "$count भजने"
                                                "Aarti" -> "$count आरत्या"
                                                "Chalisa" -> "$count चालिसा"
                                                else -> "$count पाठ"
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        AdMobBanner()
                    }
                }

                // ==================== STEP 3: AARTI & CHALISA & BHAJAN LIST ====================
                "AARTI" -> {
                    val deityAartis = aartis.filter {
                        it.deity.equals(selectedDeity, ignoreCase = true) &&
                                (selectedCategory == "ALL" || it.category.equals(selectedCategory, ignoreCase = true)) &&
                                (searchQuery.isBlank() ||
                                        it.titleEnglish.contains(searchQuery, ignoreCase = true) ||
                                        it.titleHindi.contains(searchQuery, ignoreCase = true) ||
                                        it.titleMarathi.contains(searchQuery, ignoreCase = true) ||
                                        it.lyricsMarathi.contains(searchQuery, ignoreCase = true) ||
                                        it.lyricsHindi.contains(searchQuery, ignoreCase = true) ||
                                        it.lyricsEnglish.contains(searchQuery, ignoreCase = true))
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when {
                                    selectedCategory == "Bhajan" -> when (currentLang) {
                                        "hi" -> "$selectedDeity के भजन"
                                        "mr" -> "$selectedDeity ची भजने"
                                        else -> "$selectedDeity Bhajans"
                                    }
                                    selectedCategory == "Chalisa" -> when (currentLang) {
                                        "hi" -> "$selectedDeity की चालीसा"
                                        "mr" -> "$selectedDeity च्या चालिसा"
                                        else -> "$selectedDeity Chalisas"
                                    }
                                    selectedCategory == "Aarti" -> when (currentLang) {
                                        "hi" -> "$selectedDeity की आरतियाँ"
                                        "mr" -> "$selectedDeity च्या आरत्या"
                                        else -> "$selectedDeity Aartis"
                                    }
                                    else -> when (currentLang) {
                                        "hi" -> "$selectedDeity: आरती, चालीसा व भजन"
                                        "mr" -> "$selectedDeity: आरती, चालिसा व भजने"
                                        else -> "$selectedDeity Aartis & Bhajans"
                                    }
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            TextButton(onClick = {
                                viewModel.setSearchQuery("")
                                step = "DEITY"
                            }) {
                                Text(
                                    text = when (currentLang) {
                                        "hi" -> "देव बदलें"
                                        "mr" -> "देव बदला"
                                        else -> "Change Deity"
                                    },
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Search Bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    when (currentLang) {
                                        "hi" -> "आरती, चालीसा या भजन खोजें..."
                                        "mr" -> "आरती, चालिसा किंवा भजन शोधा..."
                                        else -> "Search aarti, chalisa or bhajan..."
                                    }
                                )
                            },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                                    }
                                }
                            },
                            shape = RoundedCornerShape(24.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Category Filter Chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val categories = listOf(
                                "ALL" to when (currentLang) { "hi" -> "सभी"; "mr" -> "सर्व"; else -> "All" },
                                "Aarti" to when (currentLang) { "hi" -> "आरती"; "mr" -> "आरती"; else -> "Aarti" },
                                "Chalisa" to when (currentLang) { "hi" -> "चालीसा"; "mr" -> "चालिसा"; else -> "Chalisa" },
                                "Bhajan" to when (currentLang) { "hi" -> "भजन"; "mr" -> "भजन"; else -> "Bhajan" }
                            )

                            categories.forEach { (catKey, label) ->
                                val isCatSelected = selectedCategory == catKey
                                FilterChip(
                                    selected = isCatSelected,
                                    onClick = { viewModel.setSelectedCategory(catKey) },
                                    label = {
                                        Text(
                                            text = label,
                                            fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 12.sp
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (deityAartis.isEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = when (currentLang) {
                                            "hi" -> "इस श्रेणी में कोई परिणाम नहीं मिला"
                                            "mr" -> "या श्रेणीत कोणताही पाठ आढळला नाही"
                                            else -> "No items found in this category"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TextButton(onClick = {
                                        viewModel.setSearchQuery("")
                                        viewModel.setSelectedCategory("ALL")
                                    }) {
                                        Text(
                                            when (currentLang) {
                                                "hi" -> "सभी पाठ देखें"
                                                "mr" -> "सर्व पाठ पहा"
                                                else -> "Show All"
                                            }
                                        )
                                    }
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                items(deityAartis) { aarti ->
                                    AartiCard(
                                        aarti = aarti,
                                        currentLang = currentLang,
                                        onReadClick = { onAartiClick(aarti.id) },
                                        onFavoriteClick = { viewModel.toggleFavorite(aarti) }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        AdMobBanner()
                    }
                }
            }
        }
    }
}
}

@Composable
fun AartiCard(
    aarti: AartiEntity,
    currentLang: String,
    onReadClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    val title = when (currentLang) {
        "hi" -> aarti.titleHindi
        "mr" -> aarti.titleMarathi
        else -> aarti.titleEnglish
    }

    val (badgeText, badgeBg, badgeFg) = when (aarti.category) {
        "Bhajan" -> Triple(
            when (currentLang) { "hi" -> "🎵 भजन"; "mr" -> "🎵 भजन"; else -> "🎵 Bhajan" },
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.onPrimary
        )
        "Chalisa" -> Triple(
            when (currentLang) { "hi" -> "📜 चालीसा"; "mr" -> "📜 चालिसा"; else -> "📜 Chalisa" },
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer
        )
        else -> Triple(
            when (currentLang) { "hi" -> "🪔 आरती"; "mr" -> "🪔 आरती"; else -> "🪔 Aarti" },
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onReadClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(badgeBg.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (aarti.category) {
                        "Bhajan" -> Icons.Default.MusicNote
                        "Chalisa" -> Icons.Default.AutoStories
                        else -> Icons.Default.WbSunny
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = badgeBg,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = badgeText,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeFg
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (aarti.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (aarti.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
