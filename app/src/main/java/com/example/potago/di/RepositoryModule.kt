package com.example.potago.di

import com.example.potago.data.local.UserDataStore
import com.example.potago.data.remote.FirebaseAuthDataSource
import com.example.potago.data.remote.api.FlashcardApiService
import com.example.potago.data.remote.api.SentenceApiService
import com.example.potago.data.remote.api.UserApiService
import com.example.potago.data.remote.api.VideoApiService
import com.example.potago.data.remote.api.WordSetApiService
import com.example.potago.data.remote.api.SentencePatternApiService
import com.example.potago.data.repository.AuthRepositoryImpl
import com.example.potago.data.repository.FlashcardRepositoryImpl
import com.example.potago.data.repository.ItemRepositoryImpl
import com.example.potago.data.remote.api.ItemApiService
import com.example.potago.data.remote.api.StreakApiService
import com.example.potago.domain.repository.ItemRepository
import com.example.potago.data.repository.UserRepositoryImpl
import com.example.potago.data.repository.SentencePatternRepositoryImpl
import com.example.potago.data.repository.SentenceRepositoryImpl
import com.example.potago.data.repository.StreakRepositoryImpl
import com.example.potago.data.repository.VideoRepositoryImpl
import com.example.potago.data.repository.WordSetRepositoryImpl
import com.example.potago.domain.repository.AuthRepository
import com.example.potago.domain.repository.FlashcardRepository
import com.example.potago.domain.repository.UserRepository
import com.example.potago.domain.repository.SentencePatternRepository
import com.example.potago.domain.repository.SentenceRepository
import com.example.potago.domain.repository.StreakRepository
import com.example.potago.domain.repository.VideoRepository
import com.example.potago.domain.repository.WordSetRepository
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
}
