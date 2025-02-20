package com.wallpaperscraft.keby.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.wallpaperscraft.keby.data.database.BigramDatabase.Companion.BIGRAM_DB_VERSION
import com.wallpaperscraft.keby.data.database.dao.BigramDao
import com.wallpaperscraft.keby.data.database.models.BigramItemSchema

@Database(entities = [BigramItemSchema::class], version = BIGRAM_DB_VERSION, exportSchema = false)
abstract class BigramDatabase : RoomDatabase() {

    abstract fun bigramDao(): BigramDao

    companion object {
        const val DATABASE_NAME = "bigram_database"
        const val BIGRAM_DB_VERSION = 1
    }
}