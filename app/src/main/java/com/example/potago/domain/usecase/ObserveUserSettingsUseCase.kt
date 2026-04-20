package com.example.potago.domain.usecase

import com.example.potago.domain.model.Setting
import com.example.potago.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUserSettingsUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(): Flow<Setting?> {
        return repository.getSavedSetting()
    }
}
