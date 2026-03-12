package com.example.potago.data.remote.api

import com.example.potago.data.remote.dto.RegisterRequest
import com.example.potago.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApiService {

    @GET("/api/users/profile")
    suspend fun getUserProfile(): ApiResponse<UserDto>

    @POST("/api/users/register")
    suspend fun registerUser(
        @Body request: RegisterRequest
    ): ApiResponse<UserDto>
}
