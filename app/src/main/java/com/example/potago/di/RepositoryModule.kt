package com.example.potago.di

import com.example.potago.data.local.UserDataStore
import com.example.potago.data.remote.FirebaseAuthDataSource
import com.example.potago.data.remote.api.*
import com.example.potago.data.repository.*
import com.example.potago.domain.repository.*
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

    @Provides
    @Singleton
    fun provideVideoRepository(
        videoApiService: VideoApiService
    ): VideoRepository = VideoRepositoryImpl(videoApiService)

    @Provides
    @Singleton
    fun provideWordSetRepository(
        wordSetApiService: WordSetApiService
    ): WordSetRepository = WordSetRepositoryImpl(wordSetApiService)

    @Provides
    @Singleton
    fun provideSentencePatternRepository(
        sentencePatternApiService: SentencePatternApiService
    ): SentencePatternRepository = SentencePatternRepositoryImpl(sentencePatternApiService)

    @Provides
    @Singleton
    fun provideSentenceRepository(
        sentenceApiService: SentenceApiService
    ): SentenceRepository = SentenceRepositoryImpl(sentenceApiService)

    @Provides
    @Singleton
    fun provideFlashcardRepository(
        flashcardApiService: FlashcardApiService
    ): FlashcardRepository = FlashcardRepositoryImpl(flashcardApiService)

    @Provides
    @Singleton
    fun provideItemRepository(
        itemApiService: ItemApiService
    ): ItemRepository = ItemRepositoryImpl(itemApiService)

    @Provides
    @Singleton
    fun provideStreakRepository(
        streakApiService: StreakApiService,
        userDataStore: UserDataStore
    ): StreakRepository = StreakRepositoryImpl(streakApiService, userDataStore)

    @Provides
    @Singleton
    fun provideRewardRepository(
        rewardApiService: RewardApiService
    ): RewardRepository = RewardRepositoryImpl(rewardApiService)
    fun provideMatchGameRepository(
        matchGameApiService: MatchGameApiService
    ): MatchGameRepository = MatchGameRepositoryImpl(matchGameApiService)
}
