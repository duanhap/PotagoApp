package com.example.potago.data.remote.api

import com.example.potago.data.remote.dto.RegisterRequest
import com.example.potago.data.remote.dto.SettingDto
import com.example.potago.data.remote.dto.UpdateProfileRequest
import com.example.potago.data.remote.dto.UpdateUserSettingsRequest
import com.example.potago.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface UserApiService {

    @GET("/api/users/profile")
    suspend fun getUserProfile(): ApiResponse<UserDto>

    @PUT("/api/users/profile")
    suspend fun updateUserProfile(
        @Body request: UpdateProfileRequest
    ): ApiResponse<UserDto>

    @POST("/api/users/register")
    suspend fun registerUser(
        @Body request: RegisterRequest
    ): ApiResponse<UserDto>

    @GET("/api/users/settings")
    suspend fun getUserSettings(): ApiResponse<SettingDto>

    @PUT("/api/users/settings")
    suspend fun saveUserSettings(
        @Body request: UpdateUserSettingsRequest
    ): ApiResponse<SettingDto>
}
