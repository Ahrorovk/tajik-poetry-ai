package com.example.choronopoets.domain.model

data class PoetProfile(
    val id: Int? = null,
    val key: String? = null,
    val name: String,
    val bio: String,
    val nationality: String? = null,
)

