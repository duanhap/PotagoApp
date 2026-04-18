package com.example.potago.domain.repository

import com.example.potago.domain.model.Result
import com.example.potago.data.remote.dto.RewardResponseDto

interface RewardRepository {
    suspend fun claimReward(
        action: String,
        hackExperience: Boolean,
        superExperience: Boolean
    ): Result<RewardResponseDto>
}
