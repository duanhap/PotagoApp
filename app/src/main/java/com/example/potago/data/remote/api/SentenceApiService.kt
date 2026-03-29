package com.example.potago.data.remote.api

import com.example.potago.data.remote.dto.SentenceDto
import retrofit2.http.GET
import retrofit2.http.Query

interface SentenceApiService {
    @GET("/api/sentences/recent")
    suspend fun getRecentSentences(
        @Query("limit") limit: Int = 3
    ): ApiResponse<List<SentenceDto>>
}
