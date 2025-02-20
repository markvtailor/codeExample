package com.wallpaperscraft.keby.domain.repositories

import com.wallpaperscraft.keby.domain.models.ThemePojo

interface ThemeRepository {

    suspend fun getTheme(): ThemePojo

}