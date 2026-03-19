package com.example.choronopoets.dataClass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoritePoem(
    @PrimaryKey val poemId: Int,
    val poetId: Int,
    val title: String,
    val content: String,
    val savedAt: Long = System.currentTimeMillis(),
)
