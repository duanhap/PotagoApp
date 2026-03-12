package com.example.potago.domain.repository

import com.example.potago.domain.model.User
import com.example.potago.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserProfile(): Result<User>
    suspend fun registerUser(email: String, name: String): Result<User>
    
    // DataStore methods
    suspend fun saveUser(user: User)
    fun getSavedUser(): Flow<User?>
    suspend fun clearUser()
}
