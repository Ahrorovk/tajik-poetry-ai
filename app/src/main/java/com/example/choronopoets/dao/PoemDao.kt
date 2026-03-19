package com.example.choronopoets.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.choronopoets.dataClass.Poems
import kotlinx.coroutines.flow.Flow

@Dao
interface PoemDao {
    @Query("SELECT * FROM poems WHERE poetId = :poetId")
    fun getPoemsByPoet(poetId: Int): Flow<List<Poems>>

    @Query("SELECT * FROM poems WHERE id = :poemId")
    fun getPoemsById(poemId: Int): Flow<Poems>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPoem(poem: Poems)
}
