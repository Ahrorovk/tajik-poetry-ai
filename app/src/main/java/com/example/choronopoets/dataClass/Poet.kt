package com.example.choronopoets.dataClass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "poets")
data class Poet(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val centuryId: Int,
    val name: String,
    val bio: String,
    val imageUrl: String,
    val nationality: String
)
