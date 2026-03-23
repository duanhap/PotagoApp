package com.example.potago.data.repository

import com.example.potago.data.remote.api.ApiResponse
import com.example.potago.data.remote.api.SentencePatternApiService
import com.example.potago.data.remote.dto.toDomain
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.SetencePattern
import com.example.potago.domain.repository.SentencePatternRepository
import com.google.gson.Gson
import retrofit2.HttpException
import javax.inject.Inject

class SentencePatternRepositoryImpl @Inject constructor(
    private val sentencePatternApiService: SentencePatternApiService
) : SentencePatternRepository {
    private val gson = Gson()

    override suspend fun getSentencePatterns(): Result<List<SetencePattern>> {
        return try {
            val response = sentencePatternApiService.getSentencePatterns()
            if (response.success) {
                val patterns = response.data?.map { it.toDomain() } ?: emptyList()
                Result.Success(patterns)
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    override suspend fun getRecentSentencePatterns(): Result<List<SetencePattern>> {
        return try {
            val response = sentencePatternApiService.getRecentSentencePatterns()
            if (response.success) {
                val patterns = response.data?.map { it.toDomain() } ?: emptyList()
                Result.Success(patterns)
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    private fun <T> handleError(e: Exception): Result<T> {
        return if (e is HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string()
                val apiResponse = gson.fromJson(errorBody, ApiResponse::class.java)
                Result.Error(apiResponse.message ?: "Lỗi hệ thống (${e.code()})")
            } catch (_: Exception) {
                Result.Error("Lỗi: ${e.message()}")
            }
        } else {
            Result.Error(e.message ?: "Lỗi không xác định")
        }
    }
}

