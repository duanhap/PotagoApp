package com.example.potago.presentation.screen.potato

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PotatoUiState(
    val xp: Int = 0,
    val createdAtText: String = "",
    // Streak chưa có field trong `UserDto`/profile response hiện tại
    // nên tạm thời fix cứng theo yêu cầu.
    val streakText: String = "12 Days"
)

@HiltViewModel
class PotatoViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PotatoUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Sau khi login, app đã lưu user vào DataStore.
        // Ở Potato screen, chỉ cần đọc lại để show XP.
        viewModelScope.launch {
            userRepository.getSavedUser().collect { user ->
                _uiState.update { state ->
                    state.copy(
                        xp = user?.experiencePoints ?: 0,
                        createdAtText = formatCreatedAt(user?.createdAt)
                    )
                }
            }
        }
    }

    // Backend trả `created_at` dạng `YYYY-MM-DD` (hoặc có thể có thêm time),
    // UI cần `dd/MM/yyyy`.
    private fun formatCreatedAt(raw: String?): String {
        if (raw.isNullOrBlank()) return ""
        val datePart = raw.trim().take(10) // "2026-03-14" từ "2026-03-14 16:22:38"
        val parts = datePart.split("-")
        if (parts.size != 3) return raw
        val yyyy = parts[0]
        val mm = parts[1]
        val dd = parts[2]
        return "$dd/$mm/$yyyy"
    }
}

