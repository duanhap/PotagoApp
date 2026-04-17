package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.User
import com.example.potago.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(name: String?, avatar: String?): Result<User> {
        return repository.updateUserProfile(name = name, avatar = avatar)
    }
}
