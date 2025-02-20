package com.wallpaperscraft.keby.domain.usecases

import com.wallpaperscraft.keby.domain.models.SupportedLocales
import com.wallpaperscraft.keby.domain.models.DictionaryWord
import com.wallpaperscraft.keby.domain.repositories.DictionaryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern
import java.util.zip.GZIPInputStream
import javax.inject.Inject

class DictionaryUseCase @Inject constructor(
    private val repository: DictionaryRepository
) {

    suspend fun saveDictionary(dictionary: InputStream, locale: SupportedLocales) {
        withContext(Dispatchers.IO) {
            val pattern: Pattern =
                Pattern.compile("^\\s*word=([\\w\\p{L}'\"-]+),f=(\\d+),flags=(([\\w\\p{L}'\"-]+)|\\s*),originalFreq=(\\d+).*$")
            val dictionaryInput = GZIPInputStream(dictionary)
            val bufferReader =
                BufferedReader(InputStreamReader(dictionaryInput, StandardCharsets.UTF_8))

            var wordDataLine = bufferReader.readLine()


            val parsedDictionary = mutableListOf<DictionaryWord>()
            while (wordDataLine != null) {
                val matcher = pattern.matcher(wordDataLine)

                if (matcher.matches()) {
                    val word = matcher.group(1)
                    val frequency = matcher.group(2)
                    //val flags = arrayOf(matcher.group(3), matcher.group(4)) //TODO: А нужны ли нам флаги???
                    //val originalFrequency = matcher.group(5)


                    val dictionaryWord = DictionaryWord(
                        locale.name,
                        word,
                        frequency?.toInt() ?: 0,
                    )

                    parsedDictionary.add(dictionaryWord)

                }
                wordDataLine = bufferReader.readLine()

            }
            repository.saveDictionary(parsedDictionary)
        }
    }

    suspend fun getDictionary(locale: SupportedLocales): List<DictionaryWord> {
        return repository.getDictionary(locale)
    }

    fun getActiveDictionaries(): ArrayList<SupportedLocales> {
        val activeDictionaries = arrayListOf(SupportedLocales.EN)

        return activeDictionaries
    }

}