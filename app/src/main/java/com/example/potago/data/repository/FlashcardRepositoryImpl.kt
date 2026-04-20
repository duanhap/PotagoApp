package com.example.potago.data.repository

import com.example.potago.data.remote.api.ApiResponse
import com.example.potago.data.remote.api.FlashcardApiService
import com.example.potago.data.remote.dto.toDomain
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Word
import com.example.potago.domain.model.reponse.FlashcardsResponse
import com.example.potago.domain.repository.FlashcardRepository
import com.google.gson.Gson
import retrofit2.HttpException
import javax.inject.Inject

class FlashcardRepositoryImpl @Inject constructor(
    private val apiService: FlashcardApiService
) : FlashcardRepository {
    private val gson = Gson()

    override suspend fun getFlashcards(
        wordSetId: Long,
        mode: String,
        currentWordId: Long?,
        size: Int,
        filter: String
    ): Result<FlashcardsResponse> {
        return try {
            val response = apiService.getFlashcards(wordSetId, mode, currentWordId, size, filter)
            if (response.success) {
                val words = response.data?.map { it.toDomain() } ?: emptyList<Word>()
                val total = response.pagination?.total ?: words.size
                Result.Success(FlashcardsResponse(words, total))
            } else {
                Result.Error(response.message ?: "Lỗi không xác định")
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    override suspend fun updateWordStatus(wordId: Long, status: String): Result<Word> {
        return try {
            val response = apiService.updateWord(wordId, mapOf("status" to status))
            if (response.success && response.data != null) {
                Result.Success(response.data.toDomain())
            } else {
                Result.Error(response.message ?: "Lỗi không xác định")
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    private fun <T> handleError(e: Exception): Result<T> {
        return if (e is HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string()
                val apiResponse = gson.fromJson(errorBody, ApiResponse::class.java) as? ApiResponse<*>
                Result.Error(apiResponse?.message ?: "Lỗi hệ thống (${e.code()})")
            } catch (jsonEx: Exception) {
                Result.Error("Lỗi: ${e.message()}")
            }
        } else {
            Result.Error(e.message ?: "Lỗi không xác định")
        }
    }
}
