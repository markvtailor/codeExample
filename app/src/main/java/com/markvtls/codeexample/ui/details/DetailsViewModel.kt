package com.wallpaperscraft.keby.app.ui.details

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(

): ViewModel() {


    fun checkThemeState(themeId: Int): ThemeState {
        return when {
            isThemeActive(themeId) -> ThemeState.ACTIVE
            isThemeDownloaded(themeId) -> ThemeState.DOWNLOADED
            else -> ThemeState.NEW
        }
    }

    private fun isThemeActive(themeId: Int): Boolean {
        //TODO
        return  false
    }

    private fun isThemeDownloaded(themeId: Int): Boolean {
        //TODO
        return false
    }

    fun downloadTheme(themeId: Int) {
        //TODO
    }



    enum class ThemeState {
        NEW, DOWNLOADED, ACTIVE
    }

}