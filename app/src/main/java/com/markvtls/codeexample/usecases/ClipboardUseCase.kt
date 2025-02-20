package com.wallpaperscraft.keby.domain.usecases

import com.wallpaperscraft.keby.domain.models.ClipboardItem
import com.wallpaperscraft.keby.domain.repositories.ClipboardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ClipboardUseCase @Inject constructor(private val repository: ClipboardRepository) {
    suspend fun getItems(): Flow<List<ClipboardItem>> = repository.getClipboardItems()

    suspend fun addItem(clipboardText: String, pinned: Boolean = false) = repository.addClipboardItem(clipboardText, pinned)

    suspend fun pinItem(item: ClipboardItem) = repository.pinClipboardItem(item)

    suspend fun deleteItem(item: ClipboardItem) = repository.deleteClipboardItem(item)
}