package com.example.potago.data.repository

import com.example.potago.data.remote.api.RewardApiService
import com.example.potago.data.remote.dto.RewardRequest
import com.example.potago.data.remote.dto.RewardResponseDto
import com.example.potago.domain.model.Result
import com.example.potago.domain.repository.RewardRepository
import javax.inject.Inject

class RewardRepositoryImpl @Inject constructor(
    private val rewardApiService: RewardApiService
) : RewardRepository {

    override suspend fun claimReward(
        action: String,
        hackExperience: Boolean,
        superExperience: Boolean
    ): Result<RewardResponseDto> {
        return try {
            val response = rewardApiService.claimReward(
                RewardRequest(action, hackExperience, superExperience)
            )
            val data = response.data
            if (data != null) {
                Result.Success(data)
            } else {
                Result.Error(response.message.ifBlank { "No data returned" })
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }
}
