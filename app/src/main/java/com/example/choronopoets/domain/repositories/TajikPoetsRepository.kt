package com.example.choronopoets.domain.repositories

import com.example.choronopoets.domain.model.PoetProfile
import com.example.choronopoets.domain.model.TajikPoetKey

interface TajikPoetsRepository {
    fun getAllPoets(): List<TajikPoet>
    fun getPoetByKey(key: TajikPoetKey): TajikPoet
}

data class TajikPoet(
    val key: TajikPoetKey,
    val name: String,
    val shortDescription: String,
    val imageResId: Int,
    val bio: String,
    val nationality: String? = "TJ",
    /** Matching poet id in the Room `poets` table (used to load poems). */
    val roomPoetId: Int = 0,
)

fun TajikPoet.toProfile(): PoetProfile =
    PoetProfile(
        id = null,
        key = key.id,
        name = name,
        bio = bio,
        nationality = nationality,
    )

