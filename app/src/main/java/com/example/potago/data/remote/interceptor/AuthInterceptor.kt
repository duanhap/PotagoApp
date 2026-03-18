package com.example.potago.data.remote.interceptor

import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.Tasks
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val user = FirebaseAuth.getInstance().currentUser
        val requestBuilder = chain.request().newBuilder()

        if (user != null) {
            try {
                // Lấy token đồng bộ với timeout để tránh treo app nếu mạng yếu
                val task = user.getIdToken(false)
                val tokenResult = Tasks.await(task, 30, TimeUnit.SECONDS)
                val token = tokenResult.token

                if (!token.isNullOrBlank()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Nếu lỗi lấy token, có thể cho request đi tiếp không header 
                // hoặc xử lý tùy theo logic backend của bạn
            }
        }

        return chain.proceed(requestBuilder.build())
    }
}
