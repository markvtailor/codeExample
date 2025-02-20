package com.wallpaperscraft.keby.data.repositories

import com.wallpaperscraft.keby.data.source.local.SettingsSharedPreferences
import com.wallpaperscraft.keby.domain.repositories.SettingsRepository

class SettingsRepositoryImpl(
    private val settingsSharedPreferences: SettingsSharedPreferences
    ): SettingsRepository {

    override fun updateAutocorrectSettingsValue(newValue: Boolean) = settingsSharedPreferences.updateAutocorrectSettings(newValue)

    override fun updateAutocapsSettingsValue(newValue: Boolean) = settingsSharedPreferences.updateAutocapsSettings(newValue)

    override fun updateAutospaceSettingsValue(newValue: Boolean) = settingsSharedPreferences.updateAutospaceSettings(newValue)

    override fun setNumericRowKeyboardEnabled(newValue: Boolean) = settingsSharedPreferences.setNumericRowKeyboardEnabled(newValue)

    override fun updateSuggestionsRowSettingsValue(newValue: Boolean) = settingsSharedPreferences.updateSuggestionsRowSettings(newValue)

    override fun setAlternativeSymbolicKeyboardEnabled(newValue: Boolean) = settingsSharedPreferences.setAlternativeSymbolicKeyboardEnabled(newValue)

    override fun updateClickSoundSettingsValue(newValue: Boolean) = settingsSharedPreferences.updateClickSoundSettings(newValue)

    override fun updateClickVibrationSettingsValue(newValue: Boolean) = settingsSharedPreferences.updateClickVibrationSettings(newValue)

    override fun updateClickSoundVolumeSettingsValue(newValue: Float) = settingsSharedPreferences.updateClickSoundVolumeSettings(newValue)

    override fun updateClickVibrationVolumeSettingsValue(newValue: Float) = settingsSharedPreferences.updateClickVibrationVolumeSettings(newValue)


    override fun getAutocorrectSettingsValue() = settingsSharedPreferences.getSavedAutocorrectSettings()

    override fun getAutocapsSettingsValue() = settingsSharedPreferences.getSavedAutocapsSettings()

    override fun getAutospaceSettingsValue() = settingsSharedPreferences.getSavedAutospaceSettings()

    override fun isNumericRowKeyboardEnabled() = settingsSharedPreferences.isNumericRowKeyboardEnabled()

    override fun getSuggestionsRowSettingsValue() = settingsSharedPreferences.getSavedSuggestionsRowSettings()

    override fun isAlternativeSymbolicKeyboardEnabled() = settingsSharedPreferences.isAlternativeSymbolicKeyboardEnabled()

    override fun getClickSoundEnabled() = settingsSharedPreferences.getClickSoundEnabled()

    override fun getClickVibrationSettingsValue() = settingsSharedPreferences.getSavedClickVibrationSettings()

    override fun getClickSoundVolume()  = settingsSharedPreferences.getClickSoundVolume()

    override fun getClickVibrationVolumeSettingsValue() = settingsSharedPreferences.getSavedClickVibrationVolumeSettings()

}