package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AartiDao {
    @Query("SELECT * FROM aartis")
    fun getAllAartis(): Flow<List<AartiEntity>>

    @Query("SELECT * FROM aartis WHERE isFavorite = 1")
    fun getFavoriteAartis(): Flow<List<AartiEntity>>

    @Query("SELECT * FROM aartis WHERE deity = :deity")
    fun getAartisByDeity(deity: String): Flow<List<AartiEntity>>

    @Query("SELECT * FROM aartis WHERE titleEnglish LIKE '%' || :query || '%' OR titleHindi LIKE '%' || :query || '%' OR titleMarathi LIKE '%' || :query || '%' OR deity LIKE '%' || :query || '%'")
    fun searchAartis(query: String): Flow<List<AartiEntity>>

    @Query("SELECT * FROM aartis WHERE id = :id")
    suspend fun getAartiById(id: Int): AartiEntity?

    @Query("UPDATE aartis SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Int, isFavorite: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAarti(aarti: AartiEntity)

    @Query("SELECT COUNT(*) FROM aartis")
    suspend fun getCount(): Int
}
