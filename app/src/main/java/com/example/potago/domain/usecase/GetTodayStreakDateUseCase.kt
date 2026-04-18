package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.StreakDate
import com.example.potago.domain.repository.StreakRepository
import javax.inject.Inject

class GetTodayStreakDateUseCase @Inject constructor(
    private val repository: StreakRepository
) {
    suspend operator fun invoke(): Result<StreakDate?> {
        return repository.getTodayStreakDate()
    }
}
