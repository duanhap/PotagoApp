package com.example.potago.data.repository

import com.example.potago.data.local.UserDataStore
import com.example.potago.data.remote.api.ApiResponse
import com.example.potago.data.remote.api.StreakApiService
import com.example.potago.data.remote.dto.toDomain
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Streak
import com.example.potago.domain.model.StreakDate
import com.example.potago.domain.repository.StreakRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import javax.inject.Inject

class StreakRepositoryImpl @Inject constructor(
    private val apiService: StreakApiService,
    private val userDataStore: UserDataStore
) : StreakRepository {
    private val gson = Gson()

    override suspend fun getCurrentStreak(): Result<Streak?> {
        return try {
            val response = apiService.getCurrentStreak()
            if (response.success) {
                val streak = response.data?.toDomain()
                if (streak != null) userDataStore.saveStreak(streak)
                Result.Success(streak)
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    override suspend fun getTodayStreakDate(): Result<StreakDate?> {
        return try {
            val response = apiService.getTodayStreakDate()
            if (response.success) {
                val streakDate = response.data?.toDomain()
                if (streakDate != null) userDataStore.saveTodayStreakDate(streakDate)
                Result.Success(streakDate)
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    override fun getSavedStreak(): Flow<Streak?> {
        return userDataStore.getStreak()
    }

    override fun getSavedTodayStreakDate(): Flow<StreakDate?> {
        return userDataStore.getTodayStreakDate()
    }

    private fun <T> handleError(e: Exception): Result<T> {
        return if (e is HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string()
                val apiResponse = gson.fromJson(errorBody, ApiResponse::class.java)
                Result.Error(apiResponse.message ?: "Lỗi hệ thống (${e.code()})")
            } catch (jsonEx: Exception) {
                Result.Error("Lỗi: ${e.message()}")
            }
        } else {
            Result.Error(e.message ?: "Lỗi không xác định")
        }
    }
}
