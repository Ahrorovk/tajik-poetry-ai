package com.example.choronopoets.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.choronopoets.dataClass.FavoritePoem
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {

    @Query("SELECT * FROM favorites ORDER BY savedAt DESC")
    fun getFavorites(): Flow<List<FavoritePoem>>

    @Query("SELECT COUNT(*) > 0 FROM favorites WHERE poemId = :poemId")
    fun isFavorite(poemId: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(poem: FavoritePoem)

    @Query("DELETE FROM favorites WHERE poemId = :poemId")
    suspend fun removeFavorite(poemId: Int)
}
