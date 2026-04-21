package com.example.potago.data.repository

import com.example.potago.data.remote.api.ApiResponse
import com.example.potago.data.remote.api.WordSetApiService
import com.example.potago.data.remote.dto.UpdateWordSetRequest
import com.example.potago.data.remote.dto.toDomain
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Word
import com.example.potago.domain.model.WordSet
import com.example.potago.domain.repository.WordSetRepository
import com.google.gson.Gson
import retrofit2.HttpException
import javax.inject.Inject

class WordSetRepositoryImpl @Inject constructor(
    private val wordSetApiService: WordSetApiService
) : WordSetRepository {
    private val gson = Gson()

    override suspend fun getWordSets(): Result<List<WordSet>> {
        return try {
            val response = wordSetApiService.getWordSets()
            if (response.success) {
                val wordSets = response.data?.map { it.toDomain() } ?: emptyList()
                Result.Success(wordSets)
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    override suspend fun getRecentWordSets(limit: Int): Result<List<WordSet>> {
        return try {
            val response = wordSetApiService.getRecentWordSets(limit)
            if (response.success) {
                val wordSets = response.data?.map { it.toDomain() } ?: emptyList()
                Result.Success(wordSets)
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    override suspend fun getWordSetById(wordSetId: Long): Result<WordSet> {
        return try {
            val response = wordSetApiService.getWordSetById(wordSetId)
            if (response.success && response.data != null) {
                Result.Success(response.data.toDomain())
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    override suspend fun getWordsByWordSetId(wordSetId: Long): Result<List<Word>> {
        return try {
            val response = wordSetApiService.getWordsByWordSetId(wordSetId)
            if (response.success) {
                val words = response.data?.map { it.toDomain() } ?: emptyList()
                Result.Success(words)
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    override suspend fun updateWordSet(
        wordSetId: Long,
        defLangCode: String?,
        description: String?,
        isPublic: Boolean,
        name: String?,
        termLangCode: String?
    ): Result<WordSet> {
        return try {
            val request = UpdateWordSetRequest(
                defLangCode = defLangCode,
                description = description,
                isPublic = isPublic,
                name = name,
                termLangCode = termLangCode
            )
            val response = wordSetApiService.updateWordSet(wordSetId, request)
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
            } catch (jsonEx: Exception) {
                Result.Error("Lỗi: ${e.message()}")
            }
        } else {
            Result.Error(e.message ?: "Lỗi không xác định")
        }
    }
}
