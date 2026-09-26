package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AartiEntity
import com.example.viewmodel.AartiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AartiDetailScreen(
    aartiId: Int,
    viewModel: AartiViewModel,
    onBack: () -> Unit
) {
    val aartis by viewModel.allAartis.collectAsState()
    val aarti = aartis.find { it.id == aartiId }
    val currentLang by viewModel.currentLanguage.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState()
    val context = LocalContext.current

    val scrollState = rememberScrollState()
    // Calculate reading progress percentage (0.0 to 1.0)
    val scrollProgress = if (scrollState.maxValue > 0) {
        scrollState.value.toFloat() / scrollState.maxValue.toFloat()
    } else {
        0f
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = aarti?.let {
                                when (currentLang) {
                                    "hi" -> it.titleHindi
                                    "mr" -> it.titleMarathi
                                    else -> it.titleEnglish
                                }
                            } ?: "Aarti",
                            maxLines = 1
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        aarti?.let { currentAarti ->
                            // Share Button
                            IconButton(onClick = {
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
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, title)
                                    putExtra(Intent.EXTRA_TEXT, "🌸 $title 🌸\n\n$lyrics\n\nShared via Divine Aarti Sangrah App")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Aarti via"))
                            }) {
                                Icon(imageVector = Icons.Default.Share, contentDescription = "Share Aarti")
                            }

                            // Favorite Button
                            IconButton(onClick = { viewModel.toggleFavorite(currentAarti) }) {
                                Icon(
                                    imageVector = if (currentAarti.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (currentAarti.isFavorite) androidx.compose.ui.graphics.Color.Red else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                )
                // Reading Progress Linear Indicator
                LinearProgressIndicator(
                    progress = { scrollProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    ) { innerPadding ->
        if (aarti == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Aarti not found.")
            }
        } else {
            val lyrics = when (currentLang) {
                "hi" -> aarti.lyricsHindi
                "mr" -> aarti.lyricsMarathi
                else -> aarti.lyricsEnglish
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
            ) {
                // Controls bar: Language & Font size
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Language switch
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("hi" to "हिंदी", "mr" to "मराठी", "en" to "English").forEach { (code, label) ->
                            FilterChip(
                                selected = currentLang == code,
                                onClick = { viewModel.setLanguage(code) },
                                label = { Text(label, fontSize = 11.sp) }
                            )
                        }
                    }

                    // Font size controls
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = { if (fontSize > 14f) viewModel.setFontSize(fontSize - 2f) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text("A-", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Text("${fontSize.toInt()}sp", style = MaterialTheme.typography.bodySmall)
                        IconButton(
                            onClick = { if (fontSize < 28f) viewModel.setFontSize(fontSize + 2f) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text("A+", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                // Deity Banner Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_deities_divine_art_1790419718200),
                            contentDescription = aarti.deity,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(androidx.compose.ui.graphics.Color.Transparent, androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.7f))
                                ))
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "🌸 ${aarti.deity} Aarti 🌸",
                                color = androidx.compose.ui.graphics.Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Reading Progress: ${(scrollProgress * 100).toInt()}%",
                                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Lyrics Reading Area with Progress Tracking
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = lyrics,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = fontSize.sp,
                                lineHeight = (fontSize * 1.5f).sp
                            ),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
