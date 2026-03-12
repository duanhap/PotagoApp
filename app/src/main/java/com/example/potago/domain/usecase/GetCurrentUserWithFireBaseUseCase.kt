package com.example.potago.domain.usecase

import com.example.potago.domain.model.User
import com.example.potago.domain.repository.AuthRepository

class GetCurrentUserWithFireBaseUseCase(
    private val repository: AuthRepository
) {
    operator fun invoke(): User? {
        return repository.getCurrentUser()
    }
}