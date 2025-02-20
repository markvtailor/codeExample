package com.wallpaperscraft.keby.domain.repositories

interface SharedDataPreferencesRepository {

    fun checkIfFirstLaunch(): Boolean

    fun saveFirstLaunch()

    fun checkIfBuiltinDictionariesInited(): Boolean

    fun saveBuiltinDictionariesInited()

}