package com.example.potago.di

import com.example.potago.domain.repository.AuthRepository
import com.example.potago.domain.repository.FlashcardRepository
import com.example.potago.domain.repository.UserRepository
import com.example.potago.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideLoginUseCase(
        repository: AuthRepository
    ): LoginWithFireBaseUseCase = LoginWithFireBaseUseCase(repository)

    @Provides
    @Singleton
    fun provideRegisterUseCase(
        repository: AuthRepository
    ): RegisterWithFireBaseUseCase = RegisterWithFireBaseUseCase(repository)

    @Provides
    @Singleton
    fun provideGetCurrentUserUseCase(
        repository: AuthRepository
    ): GetCurrentUserWithFireBaseUseCase = GetCurrentUserWithFireBaseUseCase(repository)

    @Provides
    @Singleton
    fun provideLogoutUseCase(
        repository: AuthRepository
    ): LogoutWithFireBaseUseCase = LogoutWithFireBaseUseCase(repository)

    @Provides
    @Singleton
    fun provideGetUserProfileUseCase(
        repository: UserRepository
    ): GetUserProfileUseCase = GetUserProfileUseCase(repository)

    @Provides
    @Singleton
    fun provideRegisterUserUseCase(
        repository: UserRepository
    ): RegisterBackendUseCase = RegisterBackendUseCase(repository)

    @Provides
    @Singleton
    fun provideGetUserSettingsUseCase(
        repository: UserRepository
    ): GetUserSettingsUseCase = GetUserSettingsUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateUserSettingsUseCase(
        repository: UserRepository
    ): UpdateUserSettingsUseCase = UpdateUserSettingsUseCase(repository)

    @Provides
    @Singleton
    fun provideGetFlashcardsUseCase(
        repository: FlashcardRepository
    ): GetFlashcardsUseCase = GetFlashcardsUseCase(repository)

}
