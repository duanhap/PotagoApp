package com.example.potago.domain.usecase

import com.example.potago.domain.model.StreakDate
import com.example.potago.domain.repository.StreakRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTodayStreakDateUseCase @Inject constructor(
    private val repository: StreakRepository
) {
    operator fun invoke(): Flow<StreakDate?> {
        return repository.getSavedTodayStreakDate()
    }
}
