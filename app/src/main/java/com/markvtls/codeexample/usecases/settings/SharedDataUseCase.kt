package com.wallpaperscraft.keby.domain.usecases.settings

import com.wallpaperscraft.keby.domain.repositories.SharedDataPreferencesRepository
import javax.inject.Inject

class SharedDataUseCase @Inject constructor(
    private val repository: SharedDataPreferencesRepository
) {

    fun checkIfFirstLaunch() = repository.checkIfFirstLaunch()

    fun saveFirstLaunch() {
        repository.saveFirstLaunch()
    }

    fun checkIfBuiltinDictionariesInited() = repository.checkIfBuiltinDictionariesInited()

    fun saveBuiltinDictionariesInited() {
        repository.saveBuiltinDictionariesInited()
    }

}