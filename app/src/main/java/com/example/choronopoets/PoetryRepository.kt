package com.example.choronopoets

import com.example.choronopoets.dao.CenturyDao
import com.example.choronopoets.dao.PoemDao
import com.example.choronopoets.dao.PoetDao
import com.example.choronopoets.dataClass.Century
import com.example.choronopoets.dataClass.Poems
import com.example.choronopoets.dataClass.Poet
import kotlinx.coroutines.flow.Flow

class PoetryRepository(
    private val centuryDao: CenturyDao,
    private val poetDao: PoetDao,
    private val poemDao: PoemDao
) {
    fun getAllCenturies(): Flow<List<Century>> = centuryDao.getAllCenturies()
    fun getPoetsByCentury(centuryId: Int): Flow<List<Poet>> = poetDao.getPoetsByCentury(centuryId)
    fun getPoemsByPoet(poetId: Int): Flow<List<Poems>> = poemDao.getPoemsByPoet(poetId)
    fun getPoetById(poetId: Int): Flow<Poet> = poetDao.getPoetById(poetId)
    fun getPoemsById(poemId: Int): Flow<Poems> = poemDao.getPoemsById(poemId)
}
