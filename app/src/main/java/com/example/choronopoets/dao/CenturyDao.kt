package com.example.choronopoets.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.choronopoets.dataClass.Century
import kotlinx.coroutines.flow.Flow

@Dao
interface CenturyDao {
    @Query("SELECT * FROM centuries")
    fun getAllCenturies(): Flow<List<Century>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCentury(century: Century)
}