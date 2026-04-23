package com.example.potago.di

import android.content.Context
import com.example.potago.data.local.JobDataStore
import com.example.potago.data.local.UserDataStore
import com.example.potago.data.local.WordSetDataStore
import com.example.potago.data.local.WritingPracticeDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideUserDataStore(@ApplicationContext context: Context): UserDataStore {
        return UserDataStore(context)
    }

    @Provides
    @Singleton
    fun provideJobDataStore(@ApplicationContext context: Context): JobDataStore {
        return JobDataStore(context)
    }

    @Provides
    @Singleton
    fun provideWordSetDataStore(@ApplicationContext context: Context): WordSetDataStore {
        return WordSetDataStore(context)
    }

    @Provides
    @Singleton
    fun provideWritingPracticeDataStore(@ApplicationContext context: Context): WritingPracticeDataStore {
        return WritingPracticeDataStore(context)
    }
}
