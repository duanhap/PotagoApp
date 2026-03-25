package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Setting
import com.example.potago.domain.repository.UserRepository
import javax.inject.Inject

class GetUserSettingsUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Result<Setting> {
        return repository.getUserSettings()
    }
}

