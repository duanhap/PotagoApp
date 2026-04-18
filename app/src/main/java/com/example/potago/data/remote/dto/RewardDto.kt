package com.example.potago.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RewardResponseDto(
    @SerializedName("experience_earned") val experienceEarned: Int,
    @SerializedName("diamond_earned") val diamondEarned: Int,
    @SerializedName("new_experience") val newExperience: Int,
    @SerializedName("new_diamond") val newDiamond: Int,
    @SerializedName("streak") val streak: RewardStreakDto
)

data class RewardStreakDto(
    @SerializedName("status") val status: String,
    @SerializedName("current_length") val currentLength: Int,
    @SerializedName("experience_today") val experienceToday: Int,
    @SerializedName("experience_goal") val experienceGoal: Int
)

data class RewardRequest(
    @SerializedName("action") val action: String,
    @SerializedName("hackExperience") val hackExperience: Boolean,
    @SerializedName("superExperience") val superExperience: Boolean
)
