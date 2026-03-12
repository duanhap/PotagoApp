package com.example.potago.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseAuthDataSource(
    private val firebaseAuth: FirebaseAuth
) {

    suspend fun login(email: String, password: String): FirebaseUser {
        return suspendCancellableCoroutine { cont ->
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    cont.resume(result.user!!)
                }
                .addOnFailureListener {
                    cont.resumeWithException(it)
                }
        }
    }

    suspend fun register(email: String, password: String): FirebaseUser {
        return suspendCancellableCoroutine { cont ->
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    cont.resume(result.user!!)
                }
                .addOnFailureListener {
                    cont.resumeWithException(it)
                }
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
}