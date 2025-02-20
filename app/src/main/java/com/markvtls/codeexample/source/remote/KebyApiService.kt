package com.wallpaperscraft.keby.data.source.remote

import com.wallpaperscraft.keby.domain.models.ThemePojo
import retrofit2.http.GET

interface KebyApiService {

    @GET("")
    suspend fun getTheme(): ThemePojo

}