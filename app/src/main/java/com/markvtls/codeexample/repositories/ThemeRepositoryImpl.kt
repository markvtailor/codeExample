package com.wallpaperscraft.keby.data.repositories

import com.wallpaperscraft.keby.data.source.remote.KebyApiService
import com.wallpaperscraft.keby.domain.models.ThemePojo
import com.wallpaperscraft.keby.domain.repositories.ThemeRepository

class ThemeRepositoryImpl(
    private val kebyApiService: KebyApiService
): ThemeRepository {
    override suspend fun getTheme(): ThemePojo {
        return kebyApiService.getTheme()
    }
}