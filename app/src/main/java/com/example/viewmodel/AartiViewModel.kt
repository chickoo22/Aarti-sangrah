package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AartiEntity
import com.example.data.AartiRepository
import com.example.data.AppDatabase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AartiViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AartiRepository

    init {
        val dao = AppDatabase.getDatabase(application).aartiDao()
        repository = AartiRepository(dao)
    }

    val allAartis: StateFlow<List<AartiEntity>> = repository.allAartis
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteAartis: StateFlow<List<AartiEntity>> = repository.favoriteAartis
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedDeity = MutableStateFlow<String?>(null)
    val selectedDeity: StateFlow<String?> = _selectedDeity.asStateFlow()

    private val _currentLanguage = MutableStateFlow("hi") // "hi", "mr", "en"
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val _fontSize = MutableStateFlow(18f) // sp
    val fontSize: StateFlow<Float> = _fontSize.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _dailyReminderTime = MutableStateFlow("07:00 AM")
    val dailyReminderTime: StateFlow<String> = _dailyReminderTime.asStateFlow()

    val filteredAartis: StateFlow<List<AartiEntity>> = combine(
        allAartis,
        _searchQuery,
        _selectedDeity
    ) { aartis, query, deity ->
        aartis.filter { aarti ->
            val matchesQuery = query.isBlank() ||
                    aarti.titleEnglish.contains(query, ignoreCase = true) ||
                    aarti.titleHindi.contains(query) ||
                    aarti.titleMarathi.contains(query) ||
                    aarti.deity.contains(query, ignoreCase = true)

            val matchesDeity = deity == null || aarti.deity.equals(deity, ignoreCase = true)

            matchesQuery && matchesDeity
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedDeity(deity: String?) {
        _selectedDeity.value = if (_selectedDeity.value == deity) null else deity
    }

    fun setLanguage(lang: String) {
        _currentLanguage.value = lang
    }

    fun setFontSize(size: Float) {
        _fontSize.value = size
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun toggleFavorite(aarti: AartiEntity) {
        viewModelScope.launch {
            repository.updateFavorite(aarti.id, !aarti.isFavorite)
        }
    }

    fun setDailyReminderTime(time: String) {
        _dailyReminderTime.value = time
    }
}
