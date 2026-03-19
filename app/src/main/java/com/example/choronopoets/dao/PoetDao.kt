package com.example.choronopoets.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.choronopoets.dataClass.Poet
import kotlinx.coroutines.flow.Flow

@Dao
interface PoetDao {
    @Query("SELECT * FROM poets WHERE centuryId = :centuryId")
    fun getPoetsByCentury(centuryId: Int): Flow<List<Poet>>

    @Query("SELECT * FROM poets WHERE id = :poetId")
    fun getPoetById(poetId: Int): Flow<Poet>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPoet(poet: Poet)
}
