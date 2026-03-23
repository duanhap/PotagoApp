package com.example.potago.data.remote.dto

import com.example.potago.domain.model.Setting
import com.google.gson.annotations.SerializedName

data class SettingDto(
    @SerializedName("id") val id: Int,
    @SerializedName("notification") val notification: Boolean,
    @SerializedName("language") val language: String?,
    // Backend trả về `experience_goal`
    @SerializedName("experience_goal") val experienceGoal: Int,
    @SerializedName("user_id") val userId: Int,
)

fun SettingDto.toDomain(): Setting {
    return Setting(
        id = id,
        notification = notification,
        language = language,
        experienceGoal = experienceGoal,
        userId = userId
    )
}

data class UpdateUserSettingsRequest(
    // Backend chấp nhận bool hoặc 0/1
    val notification: Boolean,
    val language: String,
    // Backend đọc `experiencegoal` hoặc `experience_goal`
    @SerializedName("experiencegoal") val experienceGoal: Int
)

