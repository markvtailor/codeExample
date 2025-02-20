package com.wallpaperscraft.keby.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.wallpaperscraft.keby.data.database.DictionaryDatabase.Companion.DICTIONARY_DB_VERSION
import com.wallpaperscraft.keby.data.database.dao.DictionaryDao
import com.wallpaperscraft.keby.data.database.models.DictionaryItemSchema

@Database(entities = [DictionaryItemSchema::class], version = DICTIONARY_DB_VERSION, exportSchema = false )
abstract class DictionaryDatabase: RoomDatabase() {

    abstract fun dictionaryDao(): DictionaryDao

    companion object {
        const val DATABASE_NAME = "dictionary_database"
        const val DICTIONARY_DB_VERSION = 1
    }
}