package com.example.potago.data.remote.api

import com.example.potago.data.remote.dto.SentenceDto
import retrofit2.http.GET
import retrofit2.http.Query

interface SentenceApiService {
    @GET("/api/sentences")
    suspend fun getSentences(
        @Query("pattern_id") patternId: Int,
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null,
        @Query("status") status: String? = null
    ): ApiResponse<List<SentenceDto>>

    @GET("/api/sentences/{id}")
    suspend fun getSentenceById(
        @retrofit2.http.Path("id") id: Int
    ): ApiResponse<SentenceDto>

    @GET("/api/sentences/recent")
    suspend fun getRecentSentences(
        @Query("limit") limit: Int = 3
    ): ApiResponse<List<SentenceDto>>
}
