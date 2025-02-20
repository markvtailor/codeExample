package com.wallpaperscraft.keby.data.repositories

import com.wallpaperscraft.keby.data.source.local.DataSharedPreferences
import com.wallpaperscraft.keby.domain.repositories.SharedDataPreferencesRepository

class SharedDataPreferencesRepositoryImpl(
    private val dataSharedPreferences: DataSharedPreferences
) : SharedDataPreferencesRepository {

    override fun checkIfFirstLaunch(): Boolean {
        return dataSharedPreferences.checkIfFirstLaunch()
    }

    override fun saveFirstLaunch() {
        dataSharedPreferences.saveFirstLaunch()
    }

    override fun checkIfBuiltinDictionariesInited(): Boolean {
        return dataSharedPreferences.checkIfBuiltinDictionariesInited()
    }

    override fun saveBuiltinDictionariesInited() {
        dataSharedPreferences.saveBuiltinDictionariesInited()
    }

}