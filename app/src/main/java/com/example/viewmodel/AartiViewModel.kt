package com.example.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AartiDao
import com.example.data.AartiEntity
import com.example.data.AppDatabase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AartiViewModel(application: Application) : AndroidViewModel(application) {
    private val dao: AartiDao = AppDatabase.getDatabase(application).aartiDao()

    val allAartis: StateFlow<List<AartiEntity>> = dao.getAllAartis()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val favoriteAartis: StateFlow<List<AartiEntity>> = dao.getFavoriteAartis()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _currentLanguage = MutableStateFlow("mr") // Default to Marathi as requested
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val _selectedDeity = MutableStateFlow<String?>(null)
    val selectedDeity: StateFlow<String?> = _selectedDeity.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun setLanguage(lang: String) {
        _currentLanguage.value = lang
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
