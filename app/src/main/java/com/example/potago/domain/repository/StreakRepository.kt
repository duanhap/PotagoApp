package com.example.potago.domain.repository

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Streak
import com.example.potago.domain.model.StreakDate
import kotlinx.coroutines.flow.Flow

interface StreakRepository {
    suspend fun getCurrentStreak(): Result<Streak?>
    suspend fun getTodayStreakDate(): Result<StreakDate?>
    
    fun getSavedStreak(): Flow<Streak?>
    fun getSavedTodayStreakDate(): Flow<StreakDate?>
}
