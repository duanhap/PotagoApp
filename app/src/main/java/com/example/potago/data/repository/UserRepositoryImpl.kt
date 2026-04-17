package com.example.potago.data.repository

import com.example.potago.data.local.UserDataStore
import com.example.potago.data.remote.api.UserApiService
import com.example.potago.data.remote.dto.RegisterRequest
import com.example.potago.data.remote.dto.UpdateProfileRequest
import com.example.potago.data.remote.dto.UpdateUserSettingsRequest
import com.example.potago.data.remote.dto.SettingDto
import com.example.potago.data.remote.dto.toUser
import com.example.potago.data.remote.dto.toDomain
import com.example.potago.domain.model.User
import com.example.potago.domain.model.Setting
import com.example.potago.domain.model.Result
import com.google.gson.Gson
import com.example.potago.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: UserApiService,
    private val userDataStore: UserDataStore
) : UserRepository {

    private val gson = Gson()

    override suspend fun getUserProfile(): Result<User> {
        return try {
            val response = apiService.getUserProfile()
            if (response.success && response.data != null) {
                val user = response.data.toUser()
                saveUser(user)
                Result.Success(user)
            } else {
                Result.Error(response.message ?: "Unknown Error")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    override suspend fun updateUserProfile(name: String?, avatar: String?): Result<User> {
        return try {
            val response = apiService.updateUserProfile(UpdateProfileRequest(name = name, avatar = avatar, tokenFcm = null))
            if (response.success && response.data != null) {
                val user = response.data.toUser()
                saveUser(user)
                Result.Success(user)
            } else {
                Result.Error(response.message ?: "Unknown Error")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    override suspend fun registerUser(email: String, name: String): Result<User> {
        return try {
            val response = apiService.registerUser(RegisterRequest(email, name))
            if (response.success && response.data != null) {
                val user = response.data.toUser()
                saveUser(user)
                Result.Success(user)
            } else {
                Result.Error(response.message ?: "Unknown Error")
            }
        } catch (e: Exception) {
            Result.Error("Registration failed: ${e.message}")
        }
    }

    override suspend fun getUserSettings(): Result<Setting> {
        return try {
            val response = apiService.getUserSettings()
            if (response.success && response.data != null) {
                val setting = response.data.toDomain()
                userDataStore.saveSetting(setting)
                Result.Success(setting)
            } else {
                Result.Error(response.message ?: "Unknown Error")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    override suspend fun saveUserSettings(
        notification: Boolean,
        language: String,
        experienceGoal: Int
    ): Result<Setting> {
        return try {
            val request = UpdateUserSettingsRequest(
                notification = notification,
                language = language,
                experienceGoal = experienceGoal
            )
            val response = apiService.saveUserSettings(request)
            if (response.success && response.data != null) {
                val setting = response.data.toDomain()
                userDataStore.saveSetting(setting)
                Result.Success(setting)
            } else {
                Result.Error(response.message ?: "Unknown Error")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    override suspend fun saveUser(user: User) {
        userDataStore.saveUser(user)
    }

    override fun getSavedUser(): Flow<User?> {
        return userDataStore.getUser()
    }

    override fun getSavedSetting(): Flow<Setting?> {
        return userDataStore.getSetting()
    }

    override suspend fun clearUser() {
        userDataStore.clearUser()
    }
}
