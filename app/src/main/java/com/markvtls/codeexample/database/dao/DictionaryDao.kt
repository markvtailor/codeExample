package com.wallpaperscraft.keby.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.wallpaperscraft.keby.data.database.models.DictionaryItemSchema
import com.wallpaperscraft.keby.domain.models.SupportedLocales

@Dao
interface DictionaryDao {

    @Insert
    suspend fun saveDictionary(dictionary: List<DictionaryItemSchema>)

    @Query("SELECT * FROM dictionary WHERE locale = :locale")
    fun getDictionary(locale: SupportedLocales): List<DictionaryItemSchema>
}