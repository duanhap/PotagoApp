package com.example.potago.domain.repository

import com.example.potago.domain.model.ActiveItemSession
import kotlinx.coroutines.flow.Flow

interface ItemSessionRepository {
    fun observeActiveSession(): Flow<ActiveItemSession?>
    suspend fun activateItem(itemType: String, durationMs: Long)
    suspend fun checkAndExpireSession()
    suspend fun clearSession()
}
