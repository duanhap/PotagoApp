package com.example.potago.data.repository

import com.example.potago.data.local.UserDataStore
import com.example.potago.data.remote.api.UserApiService
import com.example.potago.data.remote.dto.RegisterRequest
import com.example.potago.data.remote.dto.toUser
import com.example.potago.domain.model.User
import com.example.potago.domain.model.Result
import com.example.potago.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: UserApiService,
    private val userDataStore: UserDataStore
) : UserRepository {

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

    override suspend fun saveUser(user: User) {
        userDataStore.saveUser(user)
    }

    override fun getSavedUser(): Flow<User?> {
        return userDataStore.getUser()
    }

    override suspend fun clearUser() {
        userDataStore.clearUser()
    }
}
