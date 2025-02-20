package com.wallpaperscraft.keby.domain.usecases.settings

import com.wallpaperscraft.keby.domain.repositories.SettingsRepository
import javax.inject.Inject

class UpdateSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {

    fun updateAutocorrectSettingsValue(newValue: Boolean) = repository.updateAutocorrectSettingsValue(newValue)

    fun updateAutocapsSettingsValue(newValue: Boolean) = repository.updateAutocapsSettingsValue(newValue)

    fun updateAutospaceSettingsValue(newValue: Boolean) = repository.updateAutospaceSettingsValue(newValue)

    fun setNumericRowKeyboardEnabled(newValue: Boolean) = repository.setNumericRowKeyboardEnabled(newValue)

    fun updateSuggestionsRowSettingsValue(newValue: Boolean) = repository.updateSuggestionsRowSettingsValue(newValue)

    fun setAlternativeSymbolicKeyboardEnabled(newValue: Boolean) = repository.setAlternativeSymbolicKeyboardEnabled(newValue)

    fun updateClickSoundSettingsValue(newValue: Boolean) = repository.updateClickSoundSettingsValue(newValue)

    fun updateClickVibrationSettingsValue(newValue: Boolean) = repository.updateClickVibrationSettingsValue(newValue)

    fun updateClickSoundVolumeSettingsValue(newValue: Float) = repository.updateClickSoundVolumeSettingsValue(newValue)

    fun updateClickVibrationVolumeSettingsValue(newValue: Float) = repository.updateClickVibrationVolumeSettingsValue(newValue)

}