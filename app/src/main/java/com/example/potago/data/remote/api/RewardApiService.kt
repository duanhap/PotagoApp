package com.example.potago.data.remote.api

import com.example.potago.data.remote.dto.RewardRequest
import com.example.potago.data.remote.dto.RewardResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface RewardApiService {
    @POST("/api/rewards/claim")
    suspend fun claimReward(
        @Body request: RewardRequest
    ): ApiResponse<RewardResponseDto>
}
