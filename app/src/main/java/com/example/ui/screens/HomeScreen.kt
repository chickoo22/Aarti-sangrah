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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.viewmodel.AartiViewModel
import com.example.data.AartiEntity
import com.example.ui.components.AdMobBanner

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

    var step by remember { mutableStateOf("LANG") }

    val deities = listOf(
        Triple("Ganesha", "भगवान गणेश", "श्री गणेश"),
        Triple("Shiva", "भगवान शिव", "भगवान शिव"),
        Triple("Vishnu", "भगवान विष्णु", "श्री विष्णू"),
        Triple("Hanuman", "संकटमोचन हनुमान", "संकटमोचन हनुमान"),
        Triple("Durga", "माता दुर्गा", "माता दुर्गा"),
        Triple("Shani", "शनि देव", "शनि देव"),
        Triple("Saraswati", "माता सरस्वती", "माता सरस्वती")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentLang) {
                            "hi" -> "मंत्रमया आरती चालीसा संग्रह"
                            "mr" -> "मंत्रमया आरती व चालिसा संग्रह"
                            else -> "Mantramaya Aarti & Chalisa"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = onNavigateToFavorites) {
                        Icon(Icons.Default.Favorite, contentDescription = "Favorites")
                    }
                    IconButton(onClick = onNavigateToReminders) {
                        Icon(Icons.Default.Notifications, contentDescription = "Reminders")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
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
                                    "hi" -> "Step 2: अपने आराध्य देव चुनें"
                                    "mr" -> "Step 2: आपले आराध्य दैवत निवडा"
                                    else -> "Step 2: Choose Deity"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            TextButton(onClick = { step = "LANG" }) {
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

                        Spacer(modifier = Modifier.height(12.dp))

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
                                        .height(110.dp)
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
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primaryContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.SelfImprovement,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
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

                // ==================== STEP 3: AARTI & CHALISA LIST ====================
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
                                text = when (currentLang) {
                                    "hi" -> "Step 3: $selectedDeity की आरतियाँ व चालीसा"
                                    "mr" -> "Step 3: $selectedDeity च्या आरत्या व चालिसा"
                                    else -> "Step 3: $selectedDeity Aartis & Chalisas"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            TextButton(onClick = { step = "DEITY" }) {
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
                                        "hi" -> "आरती या चालीसा खोजें..."
                                        "mr" -> "आरती किंवा चालिसा शोधा..."
                                        else -> "Search aarti or chalisa..."
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onReadClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = if (aarti.category == "Chalisa") MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = aarti.category,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (aarti.category == "Chalisa") MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
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
