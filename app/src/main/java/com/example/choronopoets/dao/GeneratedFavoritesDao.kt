package com.example.choronopoets.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.choronopoets.dataClass.GeneratedFavorite
import kotlinx.coroutines.flow.Flow

@Dao
interface GeneratedFavoritesDao {

    @Query("SELECT * FROM generated_favorites ORDER BY savedAt DESC")
    fun getAll(): Flow<List<GeneratedFavorite>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(poem: GeneratedFavorite): Long

    @Query("DELETE FROM generated_favorites WHERE id = :id")
    suspend fun delete(id: Int)
}
