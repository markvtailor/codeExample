package com.wallpaperscraft.keby.data.di

import android.content.Context
import androidx.room.Room
import com.wallpaperscraft.keby.data.database.BigramDatabase
import com.wallpaperscraft.keby.data.database.ClipboardDatabase
import com.wallpaperscraft.keby.data.database.DictionaryDatabase
import com.wallpaperscraft.keby.data.database.dao.BigramDao
import com.wallpaperscraft.keby.data.database.dao.ClipboardDao
import com.wallpaperscraft.keby.data.database.dao.DictionaryDao
import com.wallpaperscraft.keby.data.repositories.BigramRepositoryImpl
import com.wallpaperscraft.keby.data.repositories.ClipboardRepositoryImpl
import com.wallpaperscraft.keby.data.repositories.DictionaryRepositoryImpl
import com.wallpaperscraft.keby.domain.repositories.BigramRepository
import com.wallpaperscraft.keby.domain.repositories.ClipboardRepository
import com.wallpaperscraft.keby.domain.repositories.DictionaryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object KeyboardModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ClipboardDatabase {
        return Room.databaseBuilder(
            context,
            ClipboardDatabase::class.java,
            ClipboardDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideClipboardDao(clipboardDatabase: ClipboardDatabase): ClipboardDao {
        return clipboardDatabase.clipboardDao()
    }

    @Provides
    fun provideClipboardRepository(dao: ClipboardDao): ClipboardRepository {
        return ClipboardRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideDictionaryDatabase(@ApplicationContext context: Context): DictionaryDatabase {
        return Room.databaseBuilder(
            context,
            DictionaryDatabase::class.java,
            DictionaryDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideDictionaryDao(dictionaryDatabase: DictionaryDatabase): DictionaryDao {
        return dictionaryDatabase.dictionaryDao()
    }

    @Provides
    fun provideDictionaryRepository(dictionaryDao: DictionaryDao): DictionaryRepository {
        return DictionaryRepositoryImpl(dictionaryDao)
    }

    @Provides
    @Singleton
    fun provideBigramDatabase(@ApplicationContext context: Context): BigramDatabase {
        return Room.databaseBuilder(
            context,
            BigramDatabase::class.java,
            BigramDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideBigramDao(bigramDatabase: BigramDatabase): BigramDao {
        return bigramDatabase.bigramDao()
    }

    @Provides
    fun provideBigramRepository(bigramDao: BigramDao): BigramRepository {
        return BigramRepositoryImpl(bigramDao)
    }

}