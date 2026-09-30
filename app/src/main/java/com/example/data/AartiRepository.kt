package com.example.data

import kotlinx.coroutines.flow.Flow

class AartiRepository(private val dao: AartiDao) {
    val allAartis: Flow<List<AartiEntity>> = dao.getAllAartis()
    val favoriteAartis: Flow<List<AartiEntity>> = dao.getFavoriteAartis()

    suspend fun getAartiById(id: Int): AartiEntity? = dao.getAartiById(id)

    suspend fun update(aarti: AartiEntity) {
        dao.update(aarti)
    }

    suspend fun insertAll(aartis: List<AartiEntity>) {
        dao.insertAll(aartis)
    }
}
