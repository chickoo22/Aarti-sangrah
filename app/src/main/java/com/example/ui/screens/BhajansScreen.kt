package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.viewmodel.AartiViewModel
import com.example.data.AartiEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BhajansScreen(
    viewModel: AartiViewModel,
    onBhajanClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    val allAartis by viewModel.allAartis.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val bhajans = allAartis.filter { it.category.equals("Bhajan", ignoreCase = true) }

    var selectedDeityFilter by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }

    val deities = listOf(
        "ALL" to when (currentLang) { "hi" -> "सभी देवता"; "mr" -> "सर्व दैवते"; else -> "All Deities" },
        "Ganesha" to when (currentLang) { "hi" -> "गणेश जी"; "mr" -> "श्री गणेश"; else -> "Ganesha" },
        "Shiva" to when (currentLang) { "hi" -> "शिव जी"; "mr" -> "महादेव"; else -> "Shiva" },
        "Durga" to when (currentLang) { "hi" -> "माता दुर्गा"; "mr" -> "आई दुर्गा"; else -> "Durga" },
        "Hanuman" to when (currentLang) { "hi" -> "हनुमान जी"; "mr" -> "मारुतीराया"; else -> "Hanuman" },
        "Vishnu" to when (currentLang) { "hi" -> "विष्णु / विठ्ठल"; "mr" -> "विष्णू / विठ्ठल"; else -> "Vishnu / Vitthal" },
        "Rama" to when (currentLang) { "hi" -> "श्री राम"; "mr" -> "श्री राम"; else -> "Rama" },
        "Saraswati" to when (currentLang) { "hi" -> "माता सरस्वती"; "mr" -> "माता सरस्वती"; else -> "Saraswati" }
    )

    val filteredBhajans = bhajans.filter { bhajan ->
        val matchesDeity = selectedDeityFilter == "ALL" || bhajan.deity.equals(selectedDeityFilter, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                bhajan.titleEnglish.contains(searchQuery, ignoreCase = true) ||
                bhajan.titleHindi.contains(searchQuery, ignoreCase = true) ||
                bhajan.titleMarathi.contains(searchQuery, ignoreCase = true) ||
                bhajan.lyricsMarathi.contains(searchQuery, ignoreCase = true) ||
                bhajan.lyricsHindi.contains(searchQuery, ignoreCase = true)
        matchesDeity && matchesSearch
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Text(
                        text = when (currentLang) {
                            "hi" -> "🎵 दिव्य भजन संग्रह (Bhajan Section)"
                            "mr" -> "🎵 दिव्य भजन संग्रह (भजने)"
                            else -> "🎵 Divine Bhajan Section"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        when (currentLang) {
                            "hi" -> "भजन खोजें..."
                            "mr" -> "भजन शोधा..."
                            else -> "Search bhajans..."
                        }
                    )
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
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

            Spacer(modifier = Modifier.height(12.dp))

            // Deity Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(deities) { (key, label) ->
                    val isSelected = selectedDeityFilter == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedDeityFilter = key },
                        label = {
                            Text(
                                text = label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isSelected) Icons.Default.MusicNote else Icons.Default.SelfImprovement,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle / Count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (currentLang) {
                        "hi" -> "कुल भजन: ${filteredBhajans.size}"
                        "mr" -> "एकूण भजने: ${filteredBhajans.size}"
                        else -> "Total Bhajans: ${filteredBhajans.size}"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bhajan List
            if (filteredBhajans.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.MusicOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = when (currentLang) {
                                "hi" -> "कोई भजन नहीं मिला"
                                "mr" -> "कोणतीही भजने सापडली नाहीत"
                                else -> "No bhajans found"
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
                    items(filteredBhajans) { bhajan ->
                        val title = when (currentLang) {
                            "hi" -> bhajan.titleHindi.ifBlank { bhajan.titleEnglish }
                            "mr" -> bhajan.titleMarathi.ifBlank { bhajan.titleEnglish }
                            else -> bhajan.titleEnglish
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onBhajanClick(bhajan.id) },
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
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.secondaryContainer
                                        ) {
                                            Text(
                                                text = bhajan.deity,
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
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = when (currentLang) {
                                            "hi" -> "भजन • दिव्य वाणी"
                                            "mr" -> "भजन • दिव्य वाणी"
                                            else -> "Bhajan • Divine Melody"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.toggleFavorite(bhajan) }
                                ) {
                                    Icon(
                                        imageVector = if (bhajan.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (bhajan.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
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
