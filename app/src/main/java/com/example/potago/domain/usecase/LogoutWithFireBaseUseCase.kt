package com.example.potago.domain.usecase

import com.example.potago.domain.repository.AuthRepository

class LogoutWithFireBaseUseCase(private val repository: AuthRepository) {
    operator fun invoke() = repository.logout()
}