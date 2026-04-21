package com.example.potago.data.remote.api

import com.example.potago.data.remote.dto.RankDto
import com.example.potago.data.remote.dto.RegisterRequest
import com.example.potago.data.remote.dto.SettingDto
import com.example.potago.data.remote.dto.UpdateProfileRequest
import com.example.potago.data.remote.dto.UpdateUserSettingsRequest
import com.example.potago.data.remote.dto.UserDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface UserApiService {

    @GET("/api/users/profile")
    suspend fun getUserProfile(): ApiResponse<UserDto>

    @PUT("/api/users/profile")
    suspend fun updateUserProfile(
        @Body request: UpdateProfileRequest
    ): ApiResponse<UserDto>

    @Multipart
    @POST("/api/users/avatar")
    suspend fun uploadAvatar(
        @Part file: MultipartBody.Part
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

    @GET("/api/users/ranking/top")
    suspend fun getRankingTop(): ApiResponse<List<UserDto>>

    @GET("/api/users/ranking/me")
    suspend fun getMyRanking(): ApiResponse<RankDto>
}
