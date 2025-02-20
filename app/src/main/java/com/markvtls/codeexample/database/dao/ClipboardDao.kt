package com.wallpaperscraft.keby.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.wallpaperscraft.keby.data.database.models.ClipboardItemSchema
import kotlinx.coroutines.flow.Flow

@Dao
interface ClipboardDao {

    @Query("SELECT * FROM clipboard ORDER BY pinned DESC")
    fun getClipboardItems(): Flow<List<ClipboardItemSchema>>

    @Insert
    suspend fun addClipboardItem(item: ClipboardItemSchema)

    @Delete
    suspend fun deleteClipboardItem(item: ClipboardItemSchema)

    @Update
    suspend fun changeItemPinned(item: ClipboardItemSchema)

    @Query("DELETE FROM clipboard WHERE pinned = 0 AND :currentMillis - createTimeMillis > :timeToDeleteMillis")
    suspend fun deleteUnpinnedItems(currentMillis: Long, timeToDeleteMillis: Long)

}