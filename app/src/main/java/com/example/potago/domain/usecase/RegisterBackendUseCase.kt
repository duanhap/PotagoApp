package com.example.potago.domain.usecase

import com.example.potago.domain.model.User
import com.example.potago.domain.model.Result
import com.example.potago.domain.repository.UserRepository
import javax.inject.Inject

class RegisterBackendUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(email: String, name: String): Result<User> {
        return repository.registerUser(email, name)
    }
}
