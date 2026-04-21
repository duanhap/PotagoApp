package com.example.potago.data.remote.api

import com.example.potago.data.remote.dto.WordDto
import com.example.potago.data.remote.dto.WordSetDto
import com.example.potago.data.remote.dto.CreateWordSetRequest
import com.example.potago.data.remote.dto.CreateWordSetWithWordsRequest
import com.example.potago.data.remote.dto.CreateWordRequest
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.potago.data.remote.dto.UpdateWordSetRequest

interface WordSetApiService {
    @GET("/api/word-sets")
    suspend fun getWordSets(): ApiResponse<List<WordSetDto>>

    @GET("/api/word-sets/recent")
    suspend fun getRecentWordSets(
        @Query("limit") limit: Int = 3
    ): ApiResponse<List<WordSetDto>>

    @GET("/api/word-sets/by-id")
    suspend fun getWordSetById(
        @Query("word_set_id") wordSetId: Long
    ): ApiResponse<WordSetDto>

    @POST("/api/word-sets")
    suspend fun createWordSet(
        @Body request: CreateWordSetRequest
    ): ApiResponse<WordSetDto>

    @POST("/api/words/bulk")
    suspend fun createWordsBulk(
        @Body request: CreateWordSetWithWordsRequest
    ): ApiResponse<List<Any>>

    @POST("/api/words")
    suspend fun createWord(
        @Body request: CreateWordRequest
    ): ApiResponse<WordDto>

    @PUT("/api/word-sets")
    suspend fun updateWordSet(
        @Query("word_set_id") wordSetId: Long,
        @Body request: UpdateWordSetRequest
    ): ApiResponse<WordSetDto>

    @GET("/api/words")
    suspend fun getWordsByWordSetId(
        @Query("word_set_id") wordSetId: Long,
        @Query("status") status: String? = null
    ): ApiResponse<List<WordDto>>

    @DELETE("/api/words")
    suspend fun deleteWord(
        @Query("word_id") wordId: Long
    ): ApiResponse<Unit>
}
