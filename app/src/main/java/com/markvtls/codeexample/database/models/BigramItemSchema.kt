package com.wallpaperscraft.keby.data.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wallpaperscraft.keby.domain.models.BigramRow
import com.wallpaperscraft.keby.domain.models.SupportedLocales

@Entity(tableName = "bigrams")
data class BigramItemSchema(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val locale: String,
    val firstWord: String,
    val secondWord: String,
    val frequency: Long
)

fun BigramItemSchema.toModel(): BigramRow {
    return BigramRow(
        firstWord, secondWord, frequency
    )
}

fun BigramRow.toSchema(locale: SupportedLocales): BigramItemSchema {
    return BigramItemSchema(0, locale.name, firstWord, secondWord, frequency)
}
