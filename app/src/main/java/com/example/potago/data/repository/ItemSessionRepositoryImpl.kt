package com.example.potago.data.repository

import com.example.potago.data.local.UserDataStore
import com.example.potago.domain.model.ActiveItemSession
import com.example.potago.domain.repository.ItemSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ItemSessionRepositoryImpl @Inject constructor(
    private val userDataStore: UserDataStore
) : ItemSessionRepository {

    override fun observeActiveSession(): Flow<ActiveItemSession?> =
        userDataStore.getActiveItemSession()

    override suspend fun activateItem(itemType: String, durationMs: Long) {
        val existing = userDataStore.getActiveItemSession().first()
        val now = System.currentTimeMillis()

        val newSession = if (existing != null && existing.isActive && existing.itemType == itemType) {
            // Cùng loại → cộng thêm thời lượng vào phần còn lại
            existing.copy(totalDurationMs = existing.remainingMs + durationMs)
        } else {
            // Loại mới hoặc hết hạn → tạo session mới
            ActiveItemSession(
                itemType = itemType,
                startTimeMs = now,
                totalDurationMs = durationMs
            )
        }
        userDataStore.saveActiveItemSession(newSession)
    }

    override suspend fun checkAndExpireSession() {
        val session = userDataStore.getActiveItemSession().first() ?: return
        if (!session.isActive) {
            userDataStore.clearActiveItemSession()
        }
    }

    override suspend fun clearSession() {
        userDataStore.clearActiveItemSession()
    }
}
