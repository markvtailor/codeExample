package com.wallpaperscraft.keby.data.source.local

import android.content.Context
import android.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext

class SettingsSharedPreferences(@ApplicationContext context: Context) {

    private val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)

    fun updateAutocorrectSettings(value: Boolean) = sharedPrefs.edit().putBoolean(AUTOCORRECT, value).apply()

    fun updateAutocapsSettings(value: Boolean) = sharedPrefs.edit().putBoolean(AUTOCAPS, value).apply()

    fun updateAutospaceSettings(value: Boolean) = sharedPrefs.edit().putBoolean(AUTOSPACE, value).apply()

    fun setNumericRowKeyboardEnabled(value: Boolean) = sharedPrefs.edit().putBoolean(DIGITS_ROW, value).apply()

    fun updateSuggestionsRowSettings(value: Boolean) = sharedPrefs.edit().putBoolean(SUGGESTIONS_ROW, value).apply()

    fun setAlternativeSymbolicKeyboardEnabled(value: Boolean) = sharedPrefs.edit().putBoolean(ALTERNATE_SYMBOLS, value).apply()

    fun updateClickSoundSettings(value: Boolean) = sharedPrefs.edit().putBoolean(CLICK_SOUND, value).apply()

    fun updateClickVibrationSettings(value: Boolean) = sharedPrefs.edit().putBoolean(CLICK_VIBRATION, value).apply()

    fun updateClickSoundVolumeSettings(volumeValue: Float) = sharedPrefs.edit().putFloat(
        CLICK_SOUND_VOLUME, volumeValue).apply()

    fun updateClickVibrationVolumeSettings(volumeValue: Float) = sharedPrefs.edit().putFloat(
        CLICK_VIBRATION_VOLUME, volumeValue).apply()


    fun getSavedAutocorrectSettings(): Boolean = sharedPrefs.getBoolean(AUTOCORRECT, true)

    fun getSavedAutocapsSettings(): Boolean = sharedPrefs.getBoolean(AUTOCAPS, true)

    fun getSavedAutospaceSettings(): Boolean = sharedPrefs.getBoolean(AUTOSPACE, true)

    fun isNumericRowKeyboardEnabled(): Boolean = sharedPrefs.getBoolean(DIGITS_ROW, false)

    fun getSavedSuggestionsRowSettings(): Boolean = sharedPrefs.getBoolean(SUGGESTIONS_ROW, true)

    fun isAlternativeSymbolicKeyboardEnabled(): Boolean = sharedPrefs.getBoolean(ALTERNATE_SYMBOLS, true)

    fun getClickSoundEnabled(): Boolean = sharedPrefs.getBoolean(CLICK_SOUND, false)

    fun getSavedClickVibrationSettings(): Boolean = sharedPrefs.getBoolean(CLICK_VIBRATION, false)

    fun getClickSoundVolume(): Float = sharedPrefs.getFloat(CLICK_SOUND_VOLUME, 0.0F)

    fun getSavedClickVibrationVolumeSettings(): Float = sharedPrefs.getFloat(CLICK_VIBRATION_VOLUME, 0.0F)

    companion object {
        const val AUTOCORRECT = "autocorrect"
        const val AUTOCAPS = "autocaps"
        const val AUTOSPACE = "autospace"
        const val DIGITS_ROW = "digits_row"
        const val SUGGESTIONS_ROW = "suggestions_row"
        const val ALTERNATE_SYMBOLS = "alternate_symbols"
        const val CLICK_SOUND = "click_sound"
        const val CLICK_VIBRATION = "click_vibration"
        const val CLICK_SOUND_VOLUME = "click_sound_volume"
        const val CLICK_VIBRATION_VOLUME = "click_vibration_volume"
    }
}