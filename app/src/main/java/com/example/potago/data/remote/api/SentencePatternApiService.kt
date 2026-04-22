package com.example.potago.data.remote.api

import com.example.potago.data.remote.dto.CreateSentencePatternRequest
import com.example.potago.data.remote.dto.SentencePatternDto
import com.example.potago.data.remote.dto.SentencePatternListDto
import com.example.potago.data.remote.dto.UpdateSentencePatternRequest
import retrofit2.http.*

interface SentencePatternApiService {
    @GET("/api/sentence-patterns")
    suspend fun getSentencePatterns(): ApiResponse<List<SentencePatternDto>>

    @GET("/api/sentence-patterns/list")
    suspend fun getSentencePatternList(
        @Query("limit") limit: Int? = null
    ): ApiResponse<SentencePatternListDto>

    @GET("/api/sentence-patterns/recent")
    suspend fun getRecentSentencePatterns(): ApiResponse<List<SentencePatternDto>>

    @GET("/api/sentence-patterns/{id}")
    suspend fun getSentencePatternById(
        @Path("id") id: Int
    ): ApiResponse<SentencePatternDto>

    @POST("/api/sentence-patterns")
    suspend fun createSentencePattern(
        @Body request: CreateSentencePatternRequest
    ): ApiResponse<SentencePatternDto>

    @PUT("/api/sentence-patterns/{id}")
    suspend fun updateSentencePattern(
        @Path("id") id: Int,
        @Body request: UpdateSentencePatternRequest
    ): ApiResponse<SentencePatternDto>

    @DELETE("/api/sentence-patterns/{id}")
    suspend fun deleteSentencePattern(
        @Path("id") id: Int
    ): ApiResponse<Unit>
}

