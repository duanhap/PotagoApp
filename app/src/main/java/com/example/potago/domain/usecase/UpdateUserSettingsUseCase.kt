package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Setting
import com.example.potago.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserSettingsUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(
        notification: Boolean,
        language: String,
        experienceGoal: Int
    ): Result<Setting> {
        return repository.saveUserSettings(
            notification = notification,
            language = language,
            experienceGoal = experienceGoal
        )
    }
}

