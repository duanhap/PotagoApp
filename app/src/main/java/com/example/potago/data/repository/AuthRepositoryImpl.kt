package com.example.potago.data.repository

import com.example.potago.data.remote.FirebaseAuthDataSource
import com.example.potago.domain.model.User
import com.example.potago.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val dataSource: FirebaseAuthDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): User {
        val firebaseUser = dataSource.login(email, password)
        return User(
            uid = firebaseUser.uid,
            email = firebaseUser.email
        )
    }

    override suspend fun register(email: String, password: String): User {
        val firebaseUser = dataSource.register(email, password)
        return User(
            uid = firebaseUser.uid,
            email = firebaseUser.email
        )
    }

    override fun logout() {
        dataSource.logout()
    }
    override fun getCurrentUser(): User? {
        val firebaseUser = dataSource.getCurrentUser()
        return firebaseUser?.let {
            User(
                uid = it.uid,
                email = it.email
            )
        }
    }
}