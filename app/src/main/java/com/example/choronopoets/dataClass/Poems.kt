package com.example.choronopoets.dataClass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "poems")
data class Poems(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val poetId: Int,
    val title: String,
    val content: String
)
