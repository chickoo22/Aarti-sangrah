package com.example.data

import kotlinx.coroutines.flow.Flow

class AartiRepository(private val dao: AartiDao) {
    val allAartis: Flow<List<AartiEntity>> = dao.getAllAartis()
    val favoriteAartis: Flow<List<AartiEntity>> = dao.getFavoriteAartis()

    fun getAartisByDeity(deity: String): Flow<List<AartiEntity>> = dao.getAartisByDeity(deity)

    fun searchAartis(query: String): Flow<List<AartiEntity>> = dao.searchAartis(query)

    suspend fun getAartiById(id: Int): AartiEntity? = dao.getAartiById(id)

    suspend fun updateFavorite(id: Int, isFavorite: Boolean) {
        dao.updateFavorite(id, isFavorite)
    }

    suspend fun insert(aarti: AartiEntity) {
        dao.insertAarti(aarti)
    }
}
