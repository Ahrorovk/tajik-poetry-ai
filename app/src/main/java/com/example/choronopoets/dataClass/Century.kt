package com.example.choronopoets.dataClass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "centuries")
data class Century(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val century: String
)
