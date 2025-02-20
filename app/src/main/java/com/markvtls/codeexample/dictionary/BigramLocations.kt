package com.wallpaperscraft.keby.app.dictionary

import androidx.annotation.RawRes
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.domain.models.SupportedLocales

class BigramLocations {

    @RawRes
    fun getBigramLocation(locale: SupportedLocales): Int {
        return when (locale) {
            SupportedLocales.EN -> R.raw.bigram_eng
            SupportedLocales.RU -> 0
            SupportedLocales.NONE -> 0
        }
    }

}