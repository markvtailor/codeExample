package com.wallpaperscraft.keby.data.source.local

import android.content.Context
import android.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext

class DataSharedPreferences(@ApplicationContext context: Context) {

    private val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)

    fun saveFirstLaunch() {
        sharedPrefs.edit().putBoolean(FIRST_LAUNCH, false).apply()
    }

    fun checkIfFirstLaunch(): Boolean {
        return sharedPrefs.getBoolean(FIRST_LAUNCH, true)
    }

    fun saveBuiltinDictionariesInited() {
        sharedPrefs.edit().putBoolean(BUILTIN_DICTIONARIES_INITED, true).apply()
    }

    fun checkIfBuiltinDictionariesInited(): Boolean {
        return sharedPrefs.getBoolean(BUILTIN_DICTIONARIES_INITED, false)
    }

    companion object {
        private const val FIRST_LAUNCH = "first_launch"
        private const val BUILTIN_DICTIONARIES_INITED = "builtin_dictionaries_inited"
    }
}