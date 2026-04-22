package com.example.potago.data.repository

import com.example.potago.data.remote.api.ApiResponse
import com.example.potago.data.remote.api.SentencePatternApiService
import com.example.potago.data.remote.dto.CreateSentencePatternRequest
import com.example.potago.data.remote.dto.CreateSentencesRequest
import com.example.potago.data.remote.dto.SentenceInputDto
import com.example.potago.data.remote.dto.UpdateSentencePatternRequest
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

    override suspend fun createSentencePatternWithSentences(
        name: String,
        description: String,
        termLangCode: String,
        defLangCode: String,
        sentences: List<Pair<String, String>>
    ): Result<SetencePattern> {
        return try {
            val createRequest = CreateSentencePatternRequest(
                name = name,
                description = description,
                termLangCode = termLangCode,
                defLangCode = defLangCode
            )
            val patternResponse = sentencePatternApiService.createSentencePattern(createRequest)
            if (!patternResponse.success || patternResponse.data == null) {
                return Result.Error(patternResponse.message ?: "Tạo mẫu câu thất bại")
            }
            val pattern = patternResponse.data.toDomain()

            val validSentences = sentences.filter { it.first.isNotBlank() && it.second.isNotBlank() }
            if (validSentences.isNotEmpty()) {
                val sentencesRequest = CreateSentencesRequest(
                    patternId = pattern.id,
                    sentences = validSentences.map { SentenceInputDto(term = it.first, definition = it.second) }
                )
                sentencePatternApiService.createSentencesBulk(sentencesRequest)
            }
            Result.Success(pattern)
        } catch (e: Exception) {
            handleError(e)
        }
    }
    override suspend fun getSentencePatternById(id: Int): Result<SetencePattern> {
        return try {
            val response = sentencePatternApiService.getSentencePatternById(id)
            if (response.success && response.data != null) {
                Result.Success(response.data.toDomain())
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    override suspend fun createSentencePattern(
        name: String,
        description: String,
        termLangCode: String,
        defLangCode: String,
        isPublic: Boolean
    ): Result<SetencePattern> {
        return try {
            val request = CreateSentencePatternRequest(
                name = name,
                description = description,
                termLangCode = termLangCode,
                defLangCode = defLangCode,
                isPublic = isPublic
            )
            val response = sentencePatternApiService.createSentencePattern(request)
            if (response.success && response.data != null) {
                Result.Success(response.data.toDomain())
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    override suspend fun updateSentencePattern(
        id: Int,
        name: String,
        description: String,
        termLangCode: String,
        defLangCode: String,
        isPublic: Boolean
    ): Result<SetencePattern> {
        return try {
            val request = UpdateSentencePatternRequest(
                name = name,
                description = description,
                termLangCode = termLangCode,
                defLangCode = defLangCode,
                isPublic = isPublic
            )
            val response = sentencePatternApiService.updateSentencePattern(id, request)
            if (response.success && response.data != null) {
                Result.Success(response.data.toDomain())
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    override suspend fun deleteSentencePattern(id: Int): Result<Unit> {
        return try {
            val response = sentencePatternApiService.deleteSentencePattern(id)
            if (response.success) {
                Result.Success(Unit)
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

