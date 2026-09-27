package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AartiDao {
    @Query("SELECT * FROM aartis")
    fun getAllAartis(): Flow<List<AartiEntity>>

    @Query("SELECT * FROM aartis WHERE id = :id")
    suspend fun getAartiById(id: Int): AartiEntity?

    @Query("SELECT * FROM aartis WHERE isFavorite = 1")
    fun getFavoriteAartis(): Flow<List<AartiEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(aartis: List<AartiEntity>)

    @Update
    suspend fun update(aarti: AartiEntity)
}
