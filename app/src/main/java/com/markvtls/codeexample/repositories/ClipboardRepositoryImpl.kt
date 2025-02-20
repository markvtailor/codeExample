package com.wallpaperscraft.keby.data.repositories

import com.wallpaperscraft.keby.data.database.dao.ClipboardDao
import com.wallpaperscraft.keby.data.database.models.ClipboardItemSchema
import com.wallpaperscraft.keby.data.database.models.toModel
import com.wallpaperscraft.keby.data.database.models.toSchema
import com.wallpaperscraft.keby.domain.models.ClipboardItem
import com.wallpaperscraft.keby.domain.repositories.ClipboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ClipboardRepositoryImpl @Inject constructor(
    private val dao: ClipboardDao
) : ClipboardRepository {

    override var lastInsertTimeMillis: Long = 0L

    override suspend fun getClipboardItems(): Flow<List<ClipboardItem>> {
        return dao.getClipboardItems().map { list ->
            list.map { it.toModel() }
        }
    }

    override suspend fun addClipboardItem(clipboardText: String, pinned: Boolean) {
        val createTime = System.currentTimeMillis()

        // Костыль для предотвращения вставки нескольких одинаковых элементов
        // при множественном срабатывании системного коллбэка android OnPrimaryClipChangedListener
        // на некоторых моделях устройств
        if (createTime - lastInsertTimeMillis > ClipboardRepository.INSERT_DELAY) {
            lastInsertTimeMillis = createTime

            dao.addClipboardItem(
                ClipboardItemSchema(
                    text = clipboardText,
                    pinned = pinned,
                    createTimeMillis = createTime
                )
            )
        }
    }

    override suspend fun pinClipboardItem(item: ClipboardItem) {
        dao.changeItemPinned(item.toSchema(pinned = !item.pinned))
    }

    override suspend fun deleteClipboardItem(item: ClipboardItem) {
        dao.deleteClipboardItem(item.toSchema())
    }

    override suspend fun deleteUnpinnedItems(timeToDeleteMillis: Long) {
        val currentMillis = System.currentTimeMillis()

        dao.deleteUnpinnedItems(currentMillis, timeToDeleteMillis)
    }
}