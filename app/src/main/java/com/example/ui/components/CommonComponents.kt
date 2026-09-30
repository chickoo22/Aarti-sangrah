package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AartiEntity

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

    val categoryLabel = when (aarti.category) {
        "Bhajan" -> when (currentLang) {
            "hi" -> "भजन"
            "mr" -> "भजन"
            else -> "Bhajan"
        }
        "Chalisa" -> when (currentLang) {
            "hi" -> "चालीसा"
            "mr" -> "चालिसा"
            else -> "Chalisa"
        }
        else -> when (currentLang) {
            "hi" -> "आरती"
            "mr" -> "आरती"
            else -> "Aarti"
        }
    }

    val (categoryIcon, containerColor) = when (aarti.category) {
        "Bhajan" -> Icons.Default.MusicNote to MaterialTheme.colorScheme.primaryContainer
        "Chalisa" -> Icons.Default.AutoStories to MaterialTheme.colorScheme.surfaceVariant
        else -> Icons.Default.WbSunny to MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onReadClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
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
                    .background(containerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = aarti.category,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (aarti.category == "Bhajan") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = categoryLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (aarti.category == "Bhajan") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = aarti.deity,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2
                )
            }

            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (aarti.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (aarti.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}
