package com.wallpaperscraft.keby.domain.usecases


import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.wallpaperscraft.keby.domain.models.BigramRow
import com.wallpaperscraft.keby.domain.models.SupportedLocales
import com.wallpaperscraft.keby.domain.repositories.BigramRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import javax.inject.Inject

class WordBigramUseCase @Inject constructor(
    private val repository: BigramRepository
) {

    suspend fun saveBigram(bigramFile: InputStream, locale: SupportedLocales) {

        withContext(Dispatchers.IO) {
            val bigramData = csvReader().readAllWithHeader(bigramFile)

            val parsedBigramData: MutableList<BigramRow> = mutableListOf()

            bigramData.forEach {
                val data = it.toList()
                parsedBigramData.add(
                    BigramRow(
                        data[0].second, data[1].second, data[2].second.toLong()
                    )
                )
            }

            repository.saveBigram(locale, parsedBigramData)

        }

    }

    suspend fun getBigram(locale: SupportedLocales): MutableMap<String, MutableMap<String, Long>> {

        val bigramData = repository.getBigram(locale)
        val bigramMap = mutableMapOf<String, MutableMap<String, Long>>()

        bigramData.forEach { row ->
            val firstWord = row.firstWord
            val secondWord = row.secondWord

            if (firstWord.isNotEmpty() && secondWord.isNotEmpty() && firstWord.first()
                    .isLetterOrDigit() && secondWord.first().isLetterOrDigit()
            ) {

                bigramMap.computeIfAbsent(firstWord) { mutableMapOf() }[secondWord] = row.frequency
            }
        }
        return bigramMap
    }

}