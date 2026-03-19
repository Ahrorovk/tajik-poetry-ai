package com.example.choronopoets.domain.model

enum class TajikPoetKey(val id: String) {
    RUDaki("rudaki"),
    FIRDAWSI("firdavsi"),
    LOIQ_SHERALI("loiq_sherali");

    companion object {
        fun fromId(id: String): TajikPoetKey =
            entries.firstOrNull { it.id == id } ?: RUDaki
    }
}

