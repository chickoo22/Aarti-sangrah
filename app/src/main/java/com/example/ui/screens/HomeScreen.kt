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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ads.AdMobBanner
import com.example.ui.components.AartiCard
import com.example.viewmodel.AartiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AartiViewModel,
    onAartiClick: (Int) -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToReminders: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val aartis by viewModel.allAartis.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val selectedDeity by viewModel.selectedDeity.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // Navigation Step within Home:
    // "LANG" -> Select Language first
    // "DEITY" -> Select Deity second
    // "AARTI" -> Select Aarti third (strictly in the chosen language)
    var step by remember { mutableStateOf("LANG") }

    val deities = listOf(
        Triple("Ganesha", "भगवान गणेश", "श्री गणेश"),
        Triple("Shiva", "भगवान शिव", "भगवान शिव"),
        Triple("Vishnu", "भगवान विष्णु", "श्री विष्णू"),
        Triple("Hanuman", "संकटमोचन हनुमान", "संकटमोचन हनुमान"),
        Triple("Durga", "माता दुर्गा", "माता दुर्गा"),
        Triple("Laxmi", "माता लक्ष्मी", "माता लक्ष्मी"),
        Triple("Krishna", "भगवान कृष्ण", "श्री कृष्ण"),
        Triple("Rama", "भगवान राम", "श्री राम"),
        Triple("Saraswati", "माता सरस्वती", "माता सरस्वती"),
        Triple("Surya", "सूर्य देवता", "सूर्य देव"),
        Triple("Shani", "शनि देव", "शनि देव")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SelfImprovement,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (step) {
                                "LANG" -> "Step 1: Select Language"
                                "DEITY" -> when (currentLang) {
                                    "hi" -> "Step 2: भगवान चुनें"
                                    "mr" -> "Step 2: देव निवडा"
                                    else -> "Step 2: Select Deity"
                                }
                                else -> when (currentLang) {
                                    "hi" -> "Step 3: $selectedDeity आरती"
                                    "mr" -> "Step 3: $selectedDeity आरती"
                                    else -> "Step 3: $selectedDeity Aarti"
                                }
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }
                },
                navigationIcon = {
                    if (step == "DEITY") {
                        IconButton(onClick = { step = "LANG" }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back to Language")
                        }
                    } else if (step == "AARTI") {
                        IconButton(onClick = { 
                            viewModel.setSelectedDeity(null)
                            step = "DEITY" 
                        }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back to Deities")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleDarkMode() }) {
                        Icon(
                            imageVector = if (viewModel.isDarkMode.collectAsState().value) Icons.Default.WbSunny else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme"
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text(when(currentLang) { "hi" -> "होम"; "mr" -> "होम"; else -> "Home" }) },
                    selected = true,
                    onClick = {
                        step = "LANG"
                        viewModel.setSelectedDeity(null)
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
                    label = { Text(when(currentLang) { "hi" -> "पसंदीदा"; "mr" -> "आवडते"; else -> "Favorites" }) },
                    selected = false,
                    onClick = onNavigateToFavorites
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Notifications, contentDescription = "Reminders") },
                    label = { Text(when(currentLang) { "hi" -> "अनुस्मारक"; "mr" -> "स्मरण"; else -> "Reminders" }) },
                    selected = false,
                    onClick = onNavigateToReminders
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text(when(currentLang) { "hi" -> "सेटिंग्स"; "mr" -> "सेटिंग्स"; else -> "Settings" }) },
                    selected = false,
                    onClick = onNavigateToSettings
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (step) {
                // ==================== STEP 1: LANGUAGE SELECTION ====================
                "LANG" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🌸 Step 1: भाषा चुनें / Select Language 🌸",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Select your language first. All aartis will be displayed strictly in your chosen language.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(32.dp))

                        val languages = listOf(
                            Triple("hi", "हिंदी (Hindi)", "संपूर्ण आरतियाँ हिंदी में"),
                            Triple("mr", "मराठी (Marathi)", "संपूर्ण आरत्या मराठीमध्ये"),
                            Triple("en", "English", "English Transliterated Aartis")
                        )

                        languages.forEach { (code, title, desc) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        viewModel.setLanguage(code)
                                        step = "DEITY"
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Language,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = desc,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
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
                                text = when(currentLang) {
                                    "hi" -> "Step 2: अपने आराध्य भगवान चुनें"
                                    "mr" -> "Step 2: आपल्या आराध्य देवांची निवड करा"
                                    else -> "Step 2: Select Your Deity"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            TextButton(onClick = { step = "LANG" }) {
                                Text(
                                    text = when(currentLang) {
                                        "hi" -> "भाषा बदलें (Change Lang)"
                                        "mr" -> "भाषा बदला"
                                        else -> "Change Language"
                                    },
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

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

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .clickable {
                                            viewModel.setSelectedDeity(deityKey)
                                            step = "AARTI"
                                        },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    elevation = CardDefaults.cardElevation(3.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primaryContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.SelfImprovement,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
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
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        AdMobBanner()
                    }
                }

                // ==================== STEP 3: AARTI LIST FOR SELECTED DEITY ====================
                "AARTI" -> {
                    val deityAartis = aartis.filter { 
                        it.deity.equals(selectedDeity, ignoreCase = true) &&
                        (searchQuery.isBlank() || 
                         it.titleEnglish.contains(searchQuery, ignoreCase = true) ||
                         it.titleHindi.contains(searchQuery) ||
                         it.titleMarathi.contains(searchQuery) ||
                         it.lyricsHindi.contains(searchQuery))
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
                                text = when(currentLang) {
                                    "hi" -> "Step 3: $selectedDeity की आरतियाँ"
                                    "mr" -> "Step 3: $selectedDeity च्या आरत्या"
                                    else -> "Step 3: $selectedDeity Aartis"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            TextButton(onClick = { step = "DEITY" }) {
                                Text(
                                    text = when(currentLang) {
                                        "hi" -> "देव बदलें"
                                        "mr" -> "देव बदला"
                                        else -> "Change Deity"
                                    },
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Search bar to filter prayers
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    when(currentLang) {
                                        "hi" -> "आरती या प्रार्थना खोजें..."
                                        "mr" -> "आरती किंवा प्रार्थना शोधा..."
                                        else -> "Search aarti or prayer..."
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

                        Spacer(modifier = Modifier.height(12.dp))

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

                        Spacer(modifier = Modifier.height(8.dp))
                        AdMobBanner()
                    }
                }
            }
        }
    }
}
