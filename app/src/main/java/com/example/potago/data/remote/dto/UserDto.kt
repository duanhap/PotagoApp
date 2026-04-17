package com.example.potago.data.remote.dto

import com.example.potago.domain.model.User
import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("uid") val uid: String,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("experience_points") val experiencePoints: Int,
    @SerializedName("diamond") val diamond: Int,
    @SerializedName("role") val role: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("last_login") val lastLogin: String?,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("token_fcm") val tokenFCM: String?
)

fun UserDto.toUser(): User {
    return User(
        id = id,
        uid = uid,
        email = email,
        name = name,
        experiencePoints = experiencePoints,
        diamond = diamond,
        role = role,
        createdAt = createdAt,
        lastLogin = lastLogin,
        avatar = avatar,
        tokenFCM = tokenFCM
    )
}

data class RegisterRequest(
    val email: String,
    val name: String
)

data class UpdateProfileRequest(
    @SerializedName("name") val name: String?,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("token_fcm") val tokenFcm: String?
)
