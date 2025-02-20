package com.wallpaperscraft.keby.domain.repositories

import com.wallpaperscraft.keby.domain.models.BigramRow
import com.wallpaperscraft.keby.domain.models.SupportedLocales

interface BigramRepository {

    suspend fun saveBigram(locale: SupportedLocales, bigram: List<BigramRow>)

    suspend fun getBigram(locale: SupportedLocales): List<BigramRow>

}