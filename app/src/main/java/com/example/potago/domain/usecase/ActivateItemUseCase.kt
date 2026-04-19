package com.example.potago.domain.usecase

import com.example.potago.domain.model.ActiveItemSession
import com.example.potago.domain.repository.ItemSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

// Thời lượng mỗi item (ms)
object ItemDuration {
    const val SUPER_XP_MS = 15 * 60 * 1000L  // 15 phút
    const val HACK_XP_MS  = 30 * 60 * 1000L  // 30 phút
}

sealed class ActivateItemResult {
    object Success : ActivateItemResult()
    data class ConflictWithOtherItem(val activeItemType: String) : ActivateItemResult()
}

class ActivateItemUseCase @Inject constructor(
    private val repository: ItemSessionRepository
) {
    suspend operator fun invoke(itemType: String): ActivateItemResult {
        val existing = repository.observeActiveSession().first()

        // Có item khác loại đang active → conflict
        if (existing != null && existing.isActive && existing.itemType != itemType) {
            return ActivateItemResult.ConflictWithOtherItem(existing.itemType)
        }

        val durationMs = when (itemType) {
            "super_xp" -> ItemDuration.SUPER_XP_MS
            "hack_xp"  -> ItemDuration.HACK_XP_MS
            else -> return ActivateItemResult.Success
        }

        repository.activateItem(itemType, durationMs)
        return ActivateItemResult.Success
    }
}
