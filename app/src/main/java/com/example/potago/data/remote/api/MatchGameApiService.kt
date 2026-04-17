package com.example.potago.data.remote.api

import com.example.potago.data.remote.dto.BestTimeDto
import com.example.potago.data.remote.dto.StartMatchGameRequest
import com.example.potago.data.remote.dto.StartMatchGameResponseData
import com.example.potago.data.remote.dto.SubmitMatchGameRequest
import com.example.potago.data.remote.dto.SubmitMatchGameResponseData
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MatchGameApiService {

    @POST("/api/match-games/start")
    suspend fun startGame(
        @Body request: StartMatchGameRequest
    ): ApiResponse<StartMatchGameResponseData>

    @POST("/api/match-games/submit")
    suspend fun submitResult(
        @Body request: SubmitMatchGameRequest
    ): ApiResponse<SubmitMatchGameResponseData>

    @GET("/api/match-games/best")
    suspend fun getBestTime(
        @Query("word_set_id") wordSetId: Long
    ): ApiResponse<BestTimeDto>
}
