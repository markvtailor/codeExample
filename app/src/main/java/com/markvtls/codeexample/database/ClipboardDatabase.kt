package com.wallpaperscraft.keby.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.wallpaperscraft.keby.data.database.dao.ClipboardDao
import com.wallpaperscraft.keby.data.database.models.ClipboardItemSchema

@Database(entities = [ClipboardItemSchema::class], version = ClipboardDatabase.CLIPBOARD_DB_VERSION, exportSchema = false)
abstract class ClipboardDatabase: RoomDatabase() {

    abstract fun clipboardDao(): ClipboardDao

    companion object {
        const val DATABASE_NAME = "clipboard_database"
        const val CLIPBOARD_DB_VERSION = 1
    }
}