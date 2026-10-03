package com.example.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "aartis",
    indices = [Index(value = ["titleEnglish"], unique = true)]
)
data class AartiEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val deity: String, // "Ganesha", "Shiva", "Durga", "Hanuman", "Vishnu", "Shani", "Saraswati", etc.
    val titleEnglish: String,
    val titleHindi: String,
    val titleMarathi: String,
    val lyricsEnglish: String,
    val lyricsHindi: String,
    val lyricsMarathi: String,
    val audioUrl: String = "",
    val isFavorite: Boolean = false,
    val category: String // "Aarti", "Chalisa", "Stotra", "Bhajan"
)
