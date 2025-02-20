package com.wallpaperscraft.keby.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.wallpaperscraft.keby.data.database.models.BigramItemSchema
import com.wallpaperscraft.keby.domain.models.SupportedLocales

@Dao
interface BigramDao {

    @Insert
    suspend fun saveBigram(bigram: List<BigramItemSchema>)

    @Query("SELECT * FROM bigrams WHERE locale = :locale")
    fun getBigram(locale: SupportedLocales): List<BigramItemSchema>

}