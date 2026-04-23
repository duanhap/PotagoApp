package com.example.potago.di

import com.example.potago.data.remote.FirebaseAuthDataSource
import com.example.potago.data.remote.api.*
import com.example.potago.data.remote.api.ItemApiService
import com.example.potago.data.remote.api.MatchGameApiService
import com.example.potago.data.remote.interceptor.AuthInterceptor
import com.example.potago.data.remote.interceptor.TokenAuthenticator
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

   private const val BASE_URL = "http://10.0.2.2:5000"


    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuthDataSource(
        firebaseAuth: FirebaseAuth
    ): FirebaseAuthDataSource = FirebaseAuthDataSource(firebaseAuth)

    @Provides
    @Singleton
    fun provideAuthInterceptor(): AuthInterceptor = AuthInterceptor()

    @Provides
    @Singleton
    fun provideTokenAuthenticator(): TokenAuthenticator = TokenAuthenticator()

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor) // Thêm log để debug
            .authenticator(tokenAuthenticator)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService {
        return retrofit.create(UserApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideVideoApiService(retrofit: Retrofit): VideoApiService {
        return retrofit.create(VideoApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWordSetApiService(retrofit: Retrofit): WordSetApiService {
        return retrofit.create(WordSetApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSentencePatternApiService(retrofit: Retrofit): SentencePatternApiService {
        return retrofit.create(SentencePatternApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSentenceApiService(retrofit: Retrofit): SentenceApiService {
        return retrofit.create(SentenceApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFlashcardApiService(retrofit: Retrofit): FlashcardApiService {
        return retrofit.create(FlashcardApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideItemApiService(retrofit: Retrofit): ItemApiService {
        return retrofit.create(ItemApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideStreakApiService(retrofit: Retrofit): StreakApiService {
        return retrofit.create(StreakApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRewardApiService(retrofit: Retrofit): RewardApiService {
        return retrofit.create(RewardApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMatchGameApiService(retrofit: Retrofit): MatchGameApiService {
        return retrofit.create(MatchGameApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWordOrderingApiService(retrofit: Retrofit): WordOrderingApiService {
        return retrofit.create(WordOrderingApiService::class.java)
    }
}
