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

   // Tự động phát hiện emulator hoặc real device
   // Emulator: 10.0.2.2 (localhost của máy host)
   // Real Device: IP máy tính trong cùng mạng WiFi
   private const val EMULATOR_BASE_URL = "http://10.0.2.2:5000"
   private const val REAL_DEVICE_BASE_URL = "http://192.168.0.101:5000"  // IP máy tính của bạn
   
   private val BASE_URL: String
       get() = if (isEmulator()) EMULATOR_BASE_URL else REAL_DEVICE_BASE_URL
   
   private fun isEmulator(): Boolean {
       return (android.os.Build.BRAND.startsWith("generic") && android.os.Build.DEVICE.startsWith("generic"))
               || android.os.Build.FINGERPRINT.startsWith("generic")
               || android.os.Build.FINGERPRINT.startsWith("unknown")
               || android.os.Build.HARDWARE.contains("goldfish")
               || android.os.Build.HARDWARE.contains("ranchu")
               || android.os.Build.MODEL.contains("google_sdk")
               || android.os.Build.MODEL.contains("Emulator")
               || android.os.Build.MODEL.contains("Android SDK built for x86")
               || android.os.Build.MANUFACTURER.contains("Genymotion")
               || android.os.Build.PRODUCT.contains("sdk_google")
               || android.os.Build.PRODUCT.contains("google_sdk")
               || android.os.Build.PRODUCT.contains("sdk")
               || android.os.Build.PRODUCT.contains("sdk_x86")
               || android.os.Build.PRODUCT.contains("vbox86p")
               || android.os.Build.PRODUCT.contains("emulator")
               || android.os.Build.PRODUCT.contains("simulator")
   }


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
