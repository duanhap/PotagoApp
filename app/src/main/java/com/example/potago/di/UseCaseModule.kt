package com.example.potago.di

import com.example.potago.domain.repository.*
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
    fun provideUpdateUserProfileUseCase(
        repository: UserRepository
    ): UpdateUserProfileUseCase = UpdateUserProfileUseCase(repository)

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

    @Provides
    @Singleton
    fun provideGetItemsUseCase(
        repository: ItemRepository
    ): GetItemsUseCase = GetItemsUseCase(repository)

    @Provides
    @Singleton
    fun providePurchaseItemUseCase(
        repository: ItemRepository
    ): PurchaseItemUseCase = PurchaseItemUseCase(repository)

    @Provides
    @Singleton
    fun provideUseItemUseCase(
        repository: ItemRepository
    ): UseItemUseCase = UseItemUseCase(repository)

    @Provides
    @Singleton
    fun provideGetCurrentStreakUseCase(
        repository: StreakRepository
    ): GetCurrentStreakUseCase = GetCurrentStreakUseCase(repository)

    @Provides
    @Singleton
    fun provideGetTodayStreakDateUseCase(
        repository: StreakRepository
    ): GetTodayStreakDateUseCase = GetTodayStreakDateUseCase(repository)
    
    @Provides
    @Singleton
    fun provideGetWordSetByIdUseCase(
        repository: WordSetRepository
    ): GetWordSetByIdUseCase = GetWordSetByIdUseCase(repository)
    
    @Provides
    @Singleton
    fun provideUpdateWordStatusUseCase(
        repository: FlashcardRepository
    ): UpdateWordStatusUseCase = UpdateWordStatusUseCase(repository)

    @Provides
    @Singleton
    fun provideObserveUserUseCase(
        repository: UserRepository
    ): ObserveUserUseCase = ObserveUserUseCase(repository)

    @Provides
    @Singleton
    fun provideObserveStreakUseCase(
        repository: StreakRepository
    ): ObserveStreakUseCase = ObserveStreakUseCase(repository)

    @Provides
    @Singleton
    fun provideObserveTodayStreakDateUseCase(
        repository: StreakRepository
    ): ObserveTodayStreakDateUseCase = ObserveTodayStreakDateUseCase(repository)
}
