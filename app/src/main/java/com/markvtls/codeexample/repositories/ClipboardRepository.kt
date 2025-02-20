package com.wallpaperscraft.keby.domain.repositories

import com.wallpaperscraft.keby.domain.models.ClipboardItem
import kotlinx.coroutines.flow.Flow

interface ClipboardRepository {

    var lastInsertTimeMillis: Long

    suspend fun getClipboardItems(): Flow<List<ClipboardItem>>

    suspend fun addClipboardItem(clipboardText: String, pinned: Boolean)

    suspend fun pinClipboardItem(item: ClipboardItem)

    suspend fun deleteClipboardItem(item: ClipboardItem)

    suspend fun deleteUnpinnedItems(timeToDeleteMillis: Long)

    companion object {
        const val INSERT_DELAY = 50L
    }
}