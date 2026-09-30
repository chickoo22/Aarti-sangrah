package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.viewmodel.AartiViewModel
import com.example.ui.components.AdMobBanner

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
                            "hi" -> "प्रिय आरतियाँ, चालीसा व भजन"
                            "mr" -> "आवडत्या आरत्या, चालिसा व भजने"
                            else -> "Favorite Prayers & Bhajans"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingVals ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingVals)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (favorites.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = when (currentLang) {
                            "hi" -> "कोई प्रिय आरती या चालीसा नहीं है।"
                            "mr" -> "कोणतीही आवडती आरती किंवा चालिसा नाही."
                            else -> "No favorite aartis or chalisas yet."
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
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
            AdMobBanner()
        }
    }
}
