package com.example.choronopoets.dataClass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "generated_favorites")
data class GeneratedFavorite(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val poetName: String,
    val style: String = "",
    val savedAt: Long = System.currentTimeMillis(),
)
