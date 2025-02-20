package com.wallpaperscraft.keby.domain.usecases

import com.wallpaperscraft.keby.domain.models.ThemePojo
import com.wallpaperscraft.keby.domain.repositories.ThemeRepository
import javax.inject.Inject

class ThemeApiUsecase @Inject constructor(
    private val repository: ThemeRepository
){
    //Debug
    suspend fun getTheme() {
        val theme = repository.getTheme()
        println(theme)
    }
}