package com.example.potago.data.remote.api

import com.example.potago.data.remote.dto.CreateSentenceRequest
import com.example.potago.data.remote.dto.SentenceDto
import com.example.potago.data.remote.dto.UpdateSentenceRequest
import retrofit2.http.*
import com.example.potago.data.remote.dto.CreateSingleSentenceRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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
        @Path("id") id: Int
    ): ApiResponse<SentenceDto>

    @GET("/api/sentences/recent")
    suspend fun getRecentSentences(
        @Query("limit") limit: Int = 3
    ): ApiResponse<List<SentenceDto>>

    @POST("/api/sentences")
    suspend fun createSentence(
        @Body request: CreateSentenceRequest
    ): ApiResponse<SentenceDto>

    @PUT("/api/sentences/{id}")
    suspend fun updateSentence(
        @Path("id") id: Int,
        @Body request: UpdateSentenceRequest
    ): ApiResponse<SentenceDto>

    @DELETE("/api/sentences/{id}")
    suspend fun deleteSentence(
        @Path("id") id: Int
    ): ApiResponse<Unit>

    @POST("/api/sentences")
    suspend fun createSentenceduan(
        @Body request: CreateSingleSentenceRequest
    ): ApiResponse<SentenceDto>
}
