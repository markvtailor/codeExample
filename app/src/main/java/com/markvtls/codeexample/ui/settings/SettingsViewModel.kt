package com.wallpaperscraft.keby.app.ui.settings

import androidx.lifecycle.ViewModel
import com.wallpaperscraft.keby.domain.usecases.settings.GetSettingsUseCase
import com.wallpaperscraft.keby.domain.usecases.settings.UpdateSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase
): ViewModel() {

    fun isAutocorrectEnabled() = getSettingsUseCase.getAutocorrectSettingsValue()

    fun isAutocapsEnabled() = getSettingsUseCase.getAutocapsSettingsValue()

    fun isAutospaceEnabled() = getSettingsUseCase.getAutospaceSettingsValue()

    fun isNumericRowKeyboardEnabled() = getSettingsUseCase.isNumericRowKeyboardEnabled()

    fun isSuggestionsRowEnabled() = getSettingsUseCase.getSuggestionsRowSettingsValue()

    fun isAlternativeSymbolicKeyboardEnabled() = getSettingsUseCase.isAlternativeSymbolicKeyboardEnabled()

    fun isClickSoundEnabled(): Boolean = getSettingsUseCase.getClickSoundEnabled()

    fun isClickVibrationEnabled(): Boolean = getSettingsUseCase.getClickVibrationSettingsValue()

    fun getClickSoundVolume(): Float = getSettingsUseCase.getClickSoundVolume()

    fun getClickVibrationVolume(): Float = getSettingsUseCase.getClickVibrationVolumeSettingsValue()

    fun updateAutocorrectSettingsValue(newValue: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
         updateSettingsUseCase.updateAutocorrectSettingsValue(newValue)
        }
    }

    fun updateAutocapsSettingsValue(newValue: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            updateSettingsUseCase.updateAutocapsSettingsValue(newValue)
        }
    }

    fun updateAutospaceSettingsValue(newValue: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            updateSettingsUseCase.updateAutospaceSettingsValue(newValue)
        }
    }

    fun setNumericRowKeyboardEnabled(newValue: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            updateSettingsUseCase.setNumericRowKeyboardEnabled(newValue)
        }
    }

    fun updateSuggestionsRowSettingsValue(newValue: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            updateSettingsUseCase.updateSuggestionsRowSettingsValue(newValue)
        }
    }

    fun setAlternativeSymbolicKeyboardEnabled(newValue: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            updateSettingsUseCase.setAlternativeSymbolicKeyboardEnabled(newValue)
        }
    }

    fun updateClickSoundSettingsValue(newValue: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            updateSettingsUseCase.updateClickSoundSettingsValue(newValue)
        }
    }

    fun updateClickVibrationSettingsValue(newValue: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            updateSettingsUseCase.updateClickVibrationSettingsValue(newValue)
        }
    }

    fun updateClickSoundVolumeSettingsValue(newValue: Float) {
        CoroutineScope(Dispatchers.IO).launch {
            updateSettingsUseCase.updateClickSoundVolumeSettingsValue(newValue / PERCENT_DENOMINATOR)
        }
    }

    fun updateClickVibrationVolumeSettingsValue(newValue: Float) {
        CoroutineScope(Dispatchers.IO).launch {
            updateSettingsUseCase.updateClickVibrationVolumeSettingsValue(newValue)
        }
    }

    companion object {
        private const val PERCENT_DENOMINATOR = 100
    }
}