package com.example.potago.data.remote.dto

import com.example.potago.domain.model.Streak
import com.example.potago.domain.model.StreakDate
import com.google.gson.annotations.SerializedName

data class StreakDto(
    @SerializedName("id") val id: Long?,
    @SerializedName("length_streak") val lengthStreak: Int?,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("current_streak") val currentStreak: Boolean?,
    @SerializedName("user_id") val userId: Int?
)

fun StreakDto.toDomain(): Streak {
    return Streak(
        id = id ?: 0L,
        lengthStreak = lengthStreak ?: 0,
        startDate = startDate.orEmpty(),
        currentStreak = currentStreak ?: false,
        userId = userId ?: 0
    )
}

data class StreakDateDto(
    @SerializedName("id") val id: Long?,
    @SerializedName("date") val date: String?,
    @SerializedName("protected_date") val protectedDate: Boolean?,
    @SerializedName("protected_by") val protectedBy: String?,
    @SerializedName("xp_earned") val xpEarned: Int?,
    @SerializedName("streak_id") val streakId: Long?,
    @SerializedName("user_id") val userId: Int?
)

fun StreakDateDto.toDomain(): StreakDate {
    return StreakDate(
        id = id ?: 0L,
        date = date.orEmpty(),
        protectedDate = protectedDate,
        protectedBy = protectedBy,
        experiencePointsEarned = xpEarned,
        streakId = streakId ?: 0L
    )
}
