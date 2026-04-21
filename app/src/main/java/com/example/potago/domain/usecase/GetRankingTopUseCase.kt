package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.User
import com.example.potago.domain.repository.UserRepository
import javax.inject.Inject

class GetRankingTopUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<List<User>> {
        return userRepository.getRankingTop()
    }
}
