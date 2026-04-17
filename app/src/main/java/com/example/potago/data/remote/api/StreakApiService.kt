package com.example.potago.data.remote.api

import com.example.potago.data.remote.dto.StreakDto
import com.example.potago.data.remote.dto.StreakDateDto
import retrofit2.http.GET

interface StreakApiService {
    @GET("/api/streaks/current")
    suspend fun getCurrentStreak(): ApiResponse<StreakDto>

    @GET("/api/streaks/today")
    suspend fun getTodayStreakDate(): ApiResponse<StreakDateDto>
}
