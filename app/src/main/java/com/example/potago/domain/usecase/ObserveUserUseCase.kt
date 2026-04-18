package com.example.potago.domain.usecase

import com.example.potago.domain.model.User
import com.example.potago.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(): Flow<User?> {
        return repository.getSavedUser()
    }
}
