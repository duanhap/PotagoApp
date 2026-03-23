package com.example.potago.data.remote.api

import com.example.potago.data.remote.dto.SentencePatternDto
import retrofit2.http.GET

interface SentencePatternApiService {
    // Backend:
    // - GET /api/sentence-patterns       => data: [pattern]
    // - GET /api/sentence-patterns/recent => data: [pattern]
    @GET("/api/sentence-patterns")
    suspend fun getSentencePatterns(): ApiResponse<List<SentencePatternDto>>

    @GET("/api/sentence-patterns/recent")
    suspend fun getRecentSentencePatterns(): ApiResponse<List<SentencePatternDto>>
}

