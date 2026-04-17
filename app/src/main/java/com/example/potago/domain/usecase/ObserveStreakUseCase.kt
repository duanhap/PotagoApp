package com.example.potago.domain.usecase

import com.example.potago.domain.model.Streak
import com.example.potago.domain.repository.StreakRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveStreakUseCase @Inject constructor(
    private val repository: StreakRepository
) {
    operator fun invoke(): Flow<Streak?> {
        return repository.getSavedStreak()
    }
}
