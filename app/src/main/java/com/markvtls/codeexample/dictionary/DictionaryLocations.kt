package com.wallpaperscraft.keby.app.dictionary

import androidx.annotation.RawRes
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.domain.models.SupportedLocales

class DictionaryLocations() {

    @RawRes
    fun getDictionaryLocation(locale: SupportedLocales): Int {
        return when (locale) {
            SupportedLocales.EN -> R.raw.en_wordlist
            SupportedLocales.RU -> 0
            SupportedLocales.NONE -> 0
        }
    }
}