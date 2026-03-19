package com.example.choronopoets.data.tajik

import com.example.choronopoets.R
import com.example.choronopoets.domain.model.TajikPoetKey
import com.example.choronopoets.domain.repositories.TajikPoet
import com.example.choronopoets.domain.repositories.TajikPoetsRepository

class TajikPoetsRepositoryImpl : TajikPoetsRepository {
    private val poets = listOf(
        TajikPoet(
            key = TajikPoetKey.RUDaki,
            name = "Рудаки",
            shortDescription = "Основоположник персидско-таджикской поэзии, мастер лирики и мудрости.",
            bio = "Абуабдулла Рудаки (858–941) — первый великий поэт персидско-таджикской литературы. " +
                "Его стихи отличаются особой музыкальностью, ясностью и глубокой человечностью. " +
                "Рудаки создавал касыды, газели и рубаи, воспевая радость жизни, природу и мудрость. " +
                "Он служил при дворе саманидского эмира Насра II в Бухаре и считается «Адамом поэтов» в персидской традиции.",
            imageResId = R.drawable.rudaki,
            nationality = "TJ",
            roomPoetId = 31,
        ),
        TajikPoet(
            key = TajikPoetKey.FIRDAWSI,
            name = "Фирдавси",
            shortDescription = "Автор «Шахнаме» — величайшего эпоса персидской литературы.",
            bio = "Абулкасим Фирдавси (940–1020) — выдающийся персидско-таджикский поэт, автор монументального " +
                "эпоса «Шахнаме» («Книга царей»). На протяжении более 30 лет он создавал это произведение " +
                "объёмом около 60 000 бейтов, описывая историю Ирана от сотворения мира до арабского завоевания. " +
                "«Шахнаме» стало символом национальной идентичности и сохранения персидского языка и культуры.",
            imageResId = R.drawable.firdausi,
            nationality = "TJ",
            roomPoetId = 32,
        ),
        TajikPoet(
            key = TajikPoetKey.LOIQ_SHERALI,
            name = "Лоик Шерали",
            shortDescription = "Современный классик, голос таджикской лирики XX века.",
            bio = "Лоик Шерали (1941–2000) — один из самых любимых таджикских поэтов современности. " +
                "Его поэзия наполнена яркими образами, искренними чувствами и любовью к родине. " +
                "Он писал о любви, природе, человеческом достоинстве и национальной памяти. " +
                "Стихи Лоика отличаются особой мелодичностью и глубоким лиризмом, снискав широкую любовь читателей в Таджикистане и за его пределами.",
            imageResId = R.drawable.sherali,
            nationality = "TJ",
            roomPoetId = 33,
        ),
    )

    override fun getAllPoets(): List<TajikPoet> = poets

    override fun getPoetByKey(key: TajikPoetKey): TajikPoet =
        poets.firstOrNull { it.key == key } ?: poets.first()
}
