package com.wallpaperscraft.keby.domain.repositories

import com.wallpaperscraft.keby.domain.models.SupportedLocales
import com.wallpaperscraft.keby.domain.models.DictionaryWord

interface DictionaryRepository {

    suspend fun saveDictionary(dictionary: List<DictionaryWord>)

    suspend fun getDictionary(locale: SupportedLocales): List<DictionaryWord>

}