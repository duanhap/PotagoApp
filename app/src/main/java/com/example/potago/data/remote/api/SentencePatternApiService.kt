package com.example.potago.data.remote.api

import com.example.potago.data.remote.dto.CreateSentencePatternRequest
import com.example.potago.data.remote.dto.CreateSentencesRequest
import com.example.potago.data.remote.dto.SentenceDto
import com.example.potago.data.remote.dto.SentencePatternDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SentencePatternApiService {
    @GET("/api/sentence-patterns")
    suspend fun getSentencePatterns(): ApiResponse<List<SentencePatternDto>>

    @GET("/api/sentence-patterns/recent")
    suspend fun getRecentSentencePatterns(): ApiResponse<List<SentencePatternDto>>

    @POST("/api/sentence-patterns")
    suspend fun createSentencePattern(
        @Body request: CreateSentencePatternRequest
    ): ApiResponse<SentencePatternDto>

    @POST("/api/sentences")
    suspend fun createSentencesBulk(
        @Body request: CreateSentencesRequest
    ): ApiResponse<List<SentenceDto>>
}

