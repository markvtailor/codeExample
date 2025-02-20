package com.wallpaperscraft.keby.domain.usecases

import com.wallpaperscraft.keby.domain.repositories.ClipboardRepository
import javax.inject.Inject

class DeleteUnpinnedClipboardItemsUseCase @Inject constructor(private val repository: ClipboardRepository) {

    suspend fun invoke(timeToDeleteMillis: Long) = repository.deleteUnpinnedItems(timeToDeleteMillis)
}