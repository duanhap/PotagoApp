package com.example.potago.domain.usecase

import com.example.potago.domain.repository.ItemSessionRepository
import javax.inject.Inject

class CheckAndExpireItemSessionUseCase @Inject constructor(
    private val repository: ItemSessionRepository
) {
    suspend operator fun invoke() = repository.checkAndExpireSession()
}
