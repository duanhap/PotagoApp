package com.example.potago.data.remote.api

import com.example.potago.data.remote.dto.StartWordOrderingRequest
import com.example.potago.data.remote.dto.StartWordOrderingResponse
import com.example.potago.data.remote.dto.SubmitWordOrderingRequest
import com.example.potago.data.remote.dto.SubmitWordOrderingResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface WordOrderingApiService {

    @POST("/api/word-ordering/start")
    suspend fun startGame(
        @Body request: StartWordOrderingRequest
    ): ApiResponse<StartWordOrderingResponse>

    @POST("/api/word-ordering/submit")
    suspend fun submitResult(
        @Body request: SubmitWordOrderingRequest
    ): ApiResponse<SubmitWordOrderingResponse>
}
