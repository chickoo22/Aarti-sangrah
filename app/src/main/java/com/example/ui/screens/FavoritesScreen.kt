package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.components.AartiCard
import com.example.viewmodel.AartiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: AartiViewModel,
    onAartiClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    val favorites by viewModel.favoriteAartis.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (currentLang) {
                            "hi" -> "पसंदीदा आरतियाँ"
                            "mr" -> "आवडत्या आरत्या"
                            else -> "Favorite Prayers"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = when (currentLang) {
                            "hi" -> "कोई पसंदीदा आरती नहीं जोड़ी गई"
                            "mr" -> "कोणतीही आवडती आरती जोडलेली नाही"
                            else -> "No favorite prayers bookmarked yet"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(favorites) { aarti ->
                    AartiCard(
                        aarti = aarti,
                        currentLang = currentLang,
                        onReadClick = { onAartiClick(aarti.id) },
                        onFavoriteClick = { viewModel.toggleFavorite(aarti) }
                    )
                }
            }
        }
    }
}
