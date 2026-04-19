package com.example.potago.domain.usecase

import com.example.potago.domain.model.ActiveItemSession
import com.example.potago.domain.repository.ItemSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveActiveItemSessionUseCase @Inject constructor(
    private val repository: ItemSessionRepository
) {
    operator fun invoke(): Flow<ActiveItemSession?> = repository.observeActiveSession()
}
