package com.wallpaperscraft.keby.data.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.wallpaperscraft.keby.data.BuildConfig
import com.wallpaperscraft.keby.data.repositories.SettingsRepositoryImpl
import com.wallpaperscraft.keby.data.repositories.SharedDataPreferencesRepositoryImpl
import com.wallpaperscraft.keby.data.repositories.ThemeRepositoryImpl
import com.wallpaperscraft.keby.data.source.local.DataSharedPreferences
import com.wallpaperscraft.keby.data.source.local.SettingsSharedPreferences
import com.wallpaperscraft.keby.data.source.remote.KebyApiService
import com.wallpaperscraft.keby.domain.repositories.SettingsRepository
import com.wallpaperscraft.keby.domain.repositories.SharedDataPreferencesRepository
import com.wallpaperscraft.keby.domain.repositories.ThemeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideSettingsSharedPreferences(@ApplicationContext context: Context): SettingsSharedPreferences {
        return SettingsSharedPreferences(context)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(sharedPreferences: SettingsSharedPreferences): SettingsRepository {
        return SettingsRepositoryImpl(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideDataSharedPreferences(@ApplicationContext context: Context): DataSharedPreferences {
        return DataSharedPreferences(context)
    }

    @Provides
    @Singleton
    fun provideDataPreferencesRepository(sharedPreferences: DataSharedPreferences): SharedDataPreferencesRepository {
        return SharedDataPreferencesRepositoryImpl(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideKebyApiService(moshi: Moshi): KebyApiService {
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BuildConfig.BASE_URL)
            .build()
            .create(KebyApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideThemeRepository(apiService: KebyApiService): ThemeRepository {
        return ThemeRepositoryImpl(apiService)
    }

    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

}