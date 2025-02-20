package com.wallpaperscraft.keby.domain.usecases

import com.wallpaperscraft.keby.domain.models.KeyboardConfiguration
import com.wallpaperscraft.keby.domain.models.SupportedLocales
import javax.inject.Inject

class KeyboardConfigurationUseCase @Inject constructor (
    //private val repository:
) {

    fun getActiveKeyboardConfigurations(): ArrayList<KeyboardConfiguration> {
        val activeKeyboardConfigurations =
            arrayListOf(
                KeyboardConfiguration(SupportedLocales.EN, mutableMapOf(), true),
                KeyboardConfiguration(SupportedLocales.RU, mutableMapOf(), false)
            )

        return activeKeyboardConfigurations
    }

}