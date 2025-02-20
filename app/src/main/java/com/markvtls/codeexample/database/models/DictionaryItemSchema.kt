package com.wallpaperscraft.keby.data.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wallpaperscraft.keby.domain.models.DictionaryWord

@Entity(tableName = "dictionary")
data class DictionaryItemSchema (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val locale: String,
    val word: String,
    val frequency: Int
    )

fun DictionaryItemSchema.toModel(): DictionaryWord {
    return DictionaryWord(this.locale, this.word, this.frequency)
}

fun DictionaryWord.toSchema(): DictionaryItemSchema {
    return DictionaryItemSchema(0, this.locale, this.word, this.frequency)
}