package com.example.potago.domain.usecase

import com.example.potago.domain.model.User
import com.example.potago.domain.model.Result
import com.example.potago.domain.repository.UserRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Result<User> {
     return repository.getUserProfile()
    }
}
