package com.example.potago.di

import com.example.potago.domain.repository.*
import com.example.potago.domain.usecase.*
import com.example.potago.domain.repository.RewardRepository
import com.example.potago.domain.repository.ItemRepository
import com.example.potago.domain.repository.ItemSessionRepository
import com.example.potago.domain.repository.WordOrderingRepository
import com.example.potago.domain.repository.SentencePatternRepository
import com.example.potago.domain.repository.SentenceRepository
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.example.potago.domain.repository.StreakRepository
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
    fun provideUpdateWordSetUseCase(
        repository: WordSetRepository
    ): UpdateWordSetUseCase = UpdateWordSetUseCase(repository)

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

    @Provides
    @Singleton
    fun provideClaimRewardUseCase(
        repository: RewardRepository
    ): ClaimRewardUseCase = ClaimRewardUseCase(repository)
    fun provideUploadAvatarUseCase(
        userRepository: UserRepository,
        @ApplicationContext context: Context
    ): UploadAvatarUseCase = UploadAvatarUseCase(userRepository, context)


    @Provides
    @Singleton
    fun provideSyncUserSessionUseCase(
        userRepository: UserRepository,
        streakRepository: StreakRepository
    ): SyncUserSessionUseCase = SyncUserSessionUseCase(userRepository, streakRepository)

    @Provides
    @Singleton
    fun provideActivateItemUseCase(
        repository: ItemSessionRepository
    ): ActivateItemUseCase = ActivateItemUseCase(repository)

    @Provides
    @Singleton
    fun provideObserveActiveItemSessionUseCase(
        repository: ItemSessionRepository
    ): ObserveActiveItemSessionUseCase = ObserveActiveItemSessionUseCase(repository)

    @Provides
    @Singleton
    fun provideCheckAndExpireItemSessionUseCase(
        repository: ItemSessionRepository
    ): CheckAndExpireItemSessionUseCase = CheckAndExpireItemSessionUseCase(repository)

    @Provides
    @Singleton
    fun provideGetWordsByWordSetIdUseCase(
        repository: WordSetRepository
    ): GetWordsByWordSetIdUseCase = GetWordsByWordSetIdUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateWordUseCase(
        repository: FlashcardRepository
    ): UpdateWordUseCase = UpdateWordUseCase(repository)

    @Provides
    @Singleton
    fun provideAddWordUseCase(
        repository: WordSetRepository
    ): AddWordUseCase = AddWordUseCase(repository)

    @Provides
    @Singleton
    fun provideDeleteWordUseCase(
        repository: WordSetRepository
    ): DeleteWordUseCase = DeleteWordUseCase(repository)

    @Provides
    @Singleton
    fun provideGetSentencePatternsUseCase(
        repository: SentencePatternRepository
    ): GetSentencePatternsUseCase = GetSentencePatternsUseCase(repository)

    @Provides
    @Singleton
    fun provideSaveSubtitleToPatternUseCase(
        repository: SentenceRepository
    ): SaveSubtitleToPatternUseCase = SaveSubtitleToPatternUseCase(repository)

    @Provides
    @Singleton
    fun provideDeleteWordSetUseCase(
        repository: WordSetRepository
    ): DeleteWordSetUseCase = DeleteWordSetUseCase(repository)

    @Provides
    @Singleton
    fun provideGetWordByIdUseCase(
        repository: WordSetRepository
    ): GetWordByIdUseCase = GetWordByIdUseCase(repository)

    @Provides
    @Singleton
    fun provideStartWordOrderingGameUseCase(
        repository: WordOrderingRepository
    ): StartWordOrderingGameUseCase = StartWordOrderingGameUseCase(repository)

    @Provides
    @Singleton
    fun provideSubmitWordOrderingResultUseCase(
        repository: WordOrderingRepository
    ): SubmitWordOrderingResultUseCase = SubmitWordOrderingResultUseCase(repository)
}
