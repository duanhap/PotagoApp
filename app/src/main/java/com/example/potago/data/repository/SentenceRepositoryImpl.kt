package com.example.potago.data.repository

import com.example.potago.data.remote.api.ApiResponse
import com.example.potago.data.remote.api.SentenceApiService
import com.example.potago.data.remote.dto.CreateSingleSentenceRequest
import com.example.potago.data.remote.dto.toDomain
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Setence
import com.example.potago.domain.repository.SentenceRepository
import com.google.gson.Gson
import retrofit2.HttpException
import javax.inject.Inject

class SentenceRepositoryImpl @Inject constructor(
    private val sentenceApiService: SentenceApiService
) : SentenceRepository {
    private val gson = Gson()

    override suspend fun getRecentSentences(limit: Int): Result<List<Setence>> {
        return try {
            val response = sentenceApiService.getRecentSentences(limit)
            if (response.success) {
                val sentences = response.data?.map { it.toDomain() } ?: emptyList()
                Result.Success(sentences)
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    override suspend fun getSentencesByPatternId(
        patternId: Int,
        page: Int?,
        pageSize: Int?,
        status: String?
    ): Result<List<Setence>> {
        return try {
            val response = sentenceApiService.getSentences(patternId, page, pageSize, status)
            if (response.success) {
                val sentences = response.data?.map { it.toDomain() } ?: emptyList()
                Result.Success(sentences)
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    override suspend fun getSentenceById(id: Int): Result<Setence> {
        return try {
            val response = sentenceApiService.getSentenceById(id)
            if (response.success && response.data != null) {
                Result.Success(response.data.toDomain())
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    override suspend fun createSentence(patternId: Int, term: String, definition: String): Result<Setence> {
        return try {
            val response = sentenceApiService.createSentence(
                CreateSingleSentenceRequest(patternId = patternId, term = term, definition = definition)
            )
            if (response.success && response.data != null) {
                Result.Success(response.data.toDomain())
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
