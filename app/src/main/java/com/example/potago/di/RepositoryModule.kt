package com.example.potago.di

import com.example.potago.data.local.UserDataStore
import com.example.potago.data.remote.FirebaseAuthDataSource
import com.example.potago.data.remote.api.UserApiService
import com.example.potago.data.repository.AuthRepositoryImpl
import com.example.potago.data.repository.UserRepositoryImpl
import com.example.potago.domain.repository.AuthRepository
import com.example.potago.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        dataSource: FirebaseAuthDataSource
    ): AuthRepository = AuthRepositoryImpl(dataSource)

    @Provides
    @Singleton
    fun provideUserRepository(
        userApiService: UserApiService,
        userDataStore: UserDataStore
    ): UserRepository = UserRepositoryImpl(userApiService, userDataStore)
}
