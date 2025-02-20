package com.wallpaperscraft.keby.data.repositories

import com.wallpaperscraft.keby.data.database.dao.DictionaryDao
import com.wallpaperscraft.keby.data.database.models.toModel
import com.wallpaperscraft.keby.data.database.models.toSchema
import com.wallpaperscraft.keby.domain.models.SupportedLocales
import com.wallpaperscraft.keby.domain.models.DictionaryWord
import com.wallpaperscraft.keby.domain.repositories.DictionaryRepository
import javax.inject.Inject

class DictionaryRepositoryImpl @Inject constructor(
    private val dictionaryDao: DictionaryDao
) : DictionaryRepository {
    override suspend fun saveDictionary(dictionary: List<DictionaryWord>) {
        dictionaryDao.saveDictionary(dictionary.map { it.toSchema() })
    }

    override suspend fun getDictionary(locale: SupportedLocales): List<DictionaryWord> {
        return dictionaryDao.getDictionary(locale).map { it.toModel() }
    }

}