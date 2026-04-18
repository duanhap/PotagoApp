package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.repository.RewardRepository
import com.example.potago.data.remote.dto.RewardResponseDto
import javax.inject.Inject

class ClaimRewardUseCase @Inject constructor(
    private val repository: RewardRepository
) {
    suspend operator fun invoke(
        action: String,
        hackExperience: Boolean,
        superExperience: Boolean
    ): Result<RewardResponseDto> {
        return repository.claimReward(action, hackExperience, superExperience)
    }
}
