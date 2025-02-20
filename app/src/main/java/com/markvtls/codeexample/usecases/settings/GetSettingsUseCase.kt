package com.wallpaperscraft.keby.domain.usecases.settings

import com.wallpaperscraft.keby.domain.repositories.SettingsRepository
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {

    fun getAutocorrectSettingsValue() = repository.getAutocorrectSettingsValue()

    fun getAutocapsSettingsValue() = repository.getAutocapsSettingsValue()

    fun getAutospaceSettingsValue() = repository.getAutospaceSettingsValue()

    fun isNumericRowKeyboardEnabled() = repository.isNumericRowKeyboardEnabled()

    fun getSuggestionsRowSettingsValue() = repository.getSuggestionsRowSettingsValue()

    fun isAlternativeSymbolicKeyboardEnabled() = repository.isAlternativeSymbolicKeyboardEnabled()

    fun getClickSoundEnabled(): Boolean = repository.getClickSoundEnabled()

    fun getClickVibrationSettingsValue(): Boolean = repository.getClickVibrationSettingsValue()

    fun getClickSoundVolume(): Float = repository.getClickSoundVolume()

    fun getClickVibrationVolumeSettingsValue(): Float = repository.getClickVibrationVolumeSettingsValue()

}