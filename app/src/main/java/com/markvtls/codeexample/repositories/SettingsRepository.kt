package com.wallpaperscraft.keby.domain.repositories

interface SettingsRepository {

    fun updateAutocorrectSettingsValue(newValue: Boolean)

    fun updateAutocapsSettingsValue(newValue: Boolean)

    fun updateAutospaceSettingsValue(newValue: Boolean)

    fun setNumericRowKeyboardEnabled(newValue: Boolean)

    fun updateSuggestionsRowSettingsValue(newValue: Boolean)

    fun setAlternativeSymbolicKeyboardEnabled(newValue: Boolean)

    fun updateClickSoundSettingsValue(newValue: Boolean)

    fun updateClickVibrationSettingsValue(newValue: Boolean)

    fun updateClickSoundVolumeSettingsValue(newValue: Float)

    fun updateClickVibrationVolumeSettingsValue(newValue: Float)

    fun getAutocorrectSettingsValue(): Boolean

    fun getAutocapsSettingsValue(): Boolean

    fun getAutospaceSettingsValue(): Boolean

    fun isNumericRowKeyboardEnabled(): Boolean

    fun getSuggestionsRowSettingsValue(): Boolean

    fun isAlternativeSymbolicKeyboardEnabled(): Boolean

    fun getClickSoundEnabled(): Boolean

    fun getClickVibrationSettingsValue(): Boolean

    fun getClickSoundVolume(): Float

    fun getClickVibrationVolumeSettingsValue(): Float

}