package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.repository.UserRepository
import javax.inject.Inject

class GetMyRankingUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<Int> {
        return userRepository.getMyRanking()
    }
}
