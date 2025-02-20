package com.wallpaperscraft.keby.data.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wallpaperscraft.keby.domain.models.ClipboardItem

@Entity(tableName = "clipboard")
data class ClipboardItemSchema(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String,
    val pinned: Boolean,
    val createTimeMillis: Long
)

fun ClipboardItemSchema.toModel(): ClipboardItem {
    return ClipboardItem(id, text, pinned, createTimeMillis)
}

fun ClipboardItem.toSchema(pinned: Boolean? = null): ClipboardItemSchema {
    return ClipboardItemSchema(id, text, pinned?: this.pinned, createTimeMillis)
}