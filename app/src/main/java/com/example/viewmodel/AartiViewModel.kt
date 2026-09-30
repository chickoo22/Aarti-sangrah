package com.example.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AartiDao
import com.example.data.AartiEntity
import com.example.data.AppDatabase
import com.example.notifications.ReminderPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AartiViewModel(application: Application) : AndroidViewModel(application) {
    private val dao: AartiDao = AppDatabase.getDatabase(application).aartiDao()
    private val reminderPrefs = ReminderPreferences(application)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = dao.getAllAartis().first()
            val seedPrayers = com.example.data.AartiSeedData.getSeedAartis()
            if (existing.isEmpty()) {
                dao.insertAll(seedPrayers)
            } else {
                val existingTitles = existing.map { it.titleEnglish }.toSet()
                val missing = seedPrayers.filter { it.titleEnglish !in existingTitles }
                if (missing.isNotEmpty()) {
                    dao.insertAll(missing)
                }
            }
        }
    }

    val allAartis: StateFlow<List<AartiEntity>> = dao.getAllAartis()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val favoriteAartis: StateFlow<List<AartiEntity>> = dao.getFavoriteAartis()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _selectedCategory = MutableStateFlow("ALL") // "ALL", "Aarti", "Chalisa", "Bhajan"
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    fun setSelectedCategory(cat: String) {
        _selectedCategory.value = cat
    }

    private val _isOnboardingCompleted = MutableStateFlow(reminderPrefs.isOnboardingCompleted)
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted.asStateFlow()

    private val _isLanguageSelected = MutableStateFlow(reminderPrefs.isLanguageSelected || reminderPrefs.isOnboardingCompleted)
    val isLanguageSelected: StateFlow<Boolean> = _isLanguageSelected.asStateFlow()

    fun completeOnboarding() {
        reminderPrefs.isOnboardingCompleted = true
        reminderPrefs.isLanguageSelected = true
        _isOnboardingCompleted.value = true
        _isLanguageSelected.value = true
    }

    fun resetOnboarding() {
        reminderPrefs.isOnboardingCompleted = false
        _isOnboardingCompleted.value = false
    }

    private val _currentLanguage = MutableStateFlow(reminderPrefs.userLanguage)
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val _selectedDeity = MutableStateFlow<String?>(null)
    val selectedDeity: StateFlow<String?> = _selectedDeity.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun setLanguage(lang: String) {
        reminderPrefs.userLanguage = lang
        reminderPrefs.isLanguageSelected = true
        _currentLanguage.value = lang
        _isLanguageSelected.value = true
    }

    fun setSelectedDeity(deity: String?) {
        _selectedDeity.value = deity
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(aarti: AartiEntity) {
        viewModelScope.launch {
            dao.update(aarti.copy(isFavorite = !aarti.isFavorite))
        }
    }

    suspend fun getAartiById(id: Int): AartiEntity? {
        return dao.getAartiById(id)
    }
}
