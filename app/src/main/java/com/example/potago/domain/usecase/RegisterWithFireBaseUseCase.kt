package com.example.potago.domain.usecase

import com.example.potago.domain.model.User
import com.example.potago.domain.repository.AuthRepository

class RegisterWithFireBaseUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): User {
        return repository.register(email, password)
    }
}