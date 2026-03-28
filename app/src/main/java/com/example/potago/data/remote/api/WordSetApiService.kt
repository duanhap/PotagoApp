package com.example.potago.data.remote.api

import com.example.potago.data.remote.dto.WordSetDto
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Query

interface WordSetApiService {
    @GET("/api/word-sets")
    suspend fun getWordSets(): ApiResponse<List<WordSetDto>>

    @GET("/api/word-sets/recent")
    suspend fun getRecentWordSets(
        @Query("limit") limit: Int = 3
    ): ApiResponse<List<WordSetDto>>

    @DELETE("/api/word-sets")
    suspend fun deleteWordSet(
        @Query("word_set_id") wordSetId: Long
    ): ApiResponse<Unit>
}
