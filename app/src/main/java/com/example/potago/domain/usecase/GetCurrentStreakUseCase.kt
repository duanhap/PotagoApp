package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Streak
import com.example.potago.domain.repository.StreakRepository
import javax.inject.Inject

class GetCurrentStreakUseCase @Inject constructor(
    private val repository: StreakRepository
) {
    suspend operator fun invoke(): Result<Streak?> {
        return repository.getCurrentStreak()
    }
}
