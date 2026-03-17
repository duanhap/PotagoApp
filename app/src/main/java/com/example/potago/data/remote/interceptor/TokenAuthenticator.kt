package com.example.potago.data.remote.interceptor

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val user = FirebaseAuth.getInstance().currentUser ?: return null

        // 🚨 chống retry vô hạn
        if (responseCount(response) >= 2) return null

        return try {
            val tokenResult = Tasks.await(user.getIdToken(true))
            val newToken = tokenResult.token

            response.request.newBuilder()
                .header("Authorization", "Bearer $newToken")
                .build()
        } catch (e: Exception) {
            null
        }
    }

    // 👇 thêm ở đây (trong class)
    private fun responseCount(response: Response): Int {
        var result = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            result++
            priorResponse = priorResponse.priorResponse
        }
        return result
    }
}