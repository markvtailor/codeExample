package com.wallpaperscraft.keby.data.repositories

import com.wallpaperscraft.keby.data.database.dao.BigramDao
import com.wallpaperscraft.keby.data.database.models.toModel
import com.wallpaperscraft.keby.data.database.models.toSchema
import com.wallpaperscraft.keby.domain.models.BigramRow
import com.wallpaperscraft.keby.domain.models.SupportedLocales
import com.wallpaperscraft.keby.domain.repositories.BigramRepository

class BigramRepositoryImpl(private val bigramDao: BigramDao) : BigramRepository {

    override suspend fun saveBigram(locale: SupportedLocales, bigram: List<BigramRow>) {
        bigramDao.saveBigram(bigram.map { it.toSchema(locale) })
    }

    override suspend fun getBigram(locale: SupportedLocales): List<BigramRow> {
        return bigramDao.getBigram(locale).map { it.toModel() }
    }
}