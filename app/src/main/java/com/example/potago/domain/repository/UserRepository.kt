package com.example.potago.domain.repository

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Setting
import com.example.potago.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserProfile(): Result<User>
    suspend fun updateUserProfile(name: String?, avatar: String?): Result<User>
    suspend fun registerUser(email: String, name: String): Result<User>
    suspend fun getUserSettings(): Result<Setting>
    suspend fun saveUserSettings(notification: Boolean, language: String, experienceGoal: Int): Result<Setting>
    
    suspend fun saveUser(user: User)
    fun getSavedUser(): Flow<User?>
    fun getSavedSetting(): Flow<Setting?>
    suspend fun clearUser()
}
