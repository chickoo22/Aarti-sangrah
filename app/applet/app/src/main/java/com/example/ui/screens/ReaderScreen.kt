package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.viewmodel.AartiViewModel
import com.example.data.AartiEntity
import com.example.ui.components.AdMobBanner
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    aartiId: Int,
    viewModel: AartiViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var aarti by remember { mutableStateOf<AartiEntity?>(null) }
    val currentLang by viewModel.currentLanguage.collectAsState()

    var fontSize by remember { mutableStateOf(16f) }
    var scrollProgress by remember { mutableStateOf(0f) }
    val scrollState = rememberScrollState()

    LaunchedEffect(aartiId) {
        aarti = viewModel.getAartiById(aartiId)
    }

    LaunchedEffect(scrollState.value, scrollState.maxValue) {
        if (scrollState.maxValue > 0) {
            scrollProgress = scrollState.value.toFloat() / scrollState.maxValue.toFloat()
        }
    }

    val currentAarti = aarti
    if (currentAarti == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val title = when (currentLang) {
        "hi" -> currentAarti.titleHindi
        "mr" -> currentAarti.titleMarathi
        else -> currentAarti.titleEnglish
    }

    val lyrics = when (currentLang) {
        "hi" -> currentAarti.lyricsHindi
        "mr" -> currentAarti.lyricsMarathi
        else -> currentAarti.lyricsEnglish
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(title, maxLines = 1, fontSize = 16.sp) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { if (fontSize > 12f) fontSize -= 2f }) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease Font")
                        }
                        IconButton(onClick = { if (fontSize < 28f) fontSize += 2f }) {
                            Icon(Icons.Default.Add, contentDescription = "Increase Font")
                        }
                        IconButton(onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, title)
                                putExtra(Intent.EXTRA_TEXT, title)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share"))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                        IconButton(onClick = {
                            viewModel.toggleFavorite(currentAarti)
                            coroutineScope.launch {
                                aarti = viewModel.getAartiById(aartiId)
                            }
                        }) {
                            Icon(
                                imageVector = if (currentAarti.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (currentAarti.isFavorite) Color.Red else Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
                LinearProgressIndicator(
                    progress = { scrollProgress },
                    modifier = Modifier.fillMaxWidth().height(3.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer
                )
            }
        }
    ) { paddingVals ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingVals)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = lyrics,
                        fontSize = fontSize.sp,
                        lineHeight = (fontSize * 1.6f).sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            AdMobBanner()
        }
    }
}
