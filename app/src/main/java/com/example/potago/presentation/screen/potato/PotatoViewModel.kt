package com.example.potago.presentation.screen.potato

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.usecase.ObserveStreakUseCase
import com.example.potago.domain.usecase.ObserveUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

data class PotatoUiState(
    val xp: Int = 0,
    val createdAtText: String = "",
    val streakCount: Int = 0
)

@HiltViewModel
class PotatoViewModel @Inject constructor(
    private val observeUserUseCase: ObserveUserUseCase,
    private val observeStreakUseCase: ObserveStreakUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PotatoUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        combine(
            observeUserUseCase(),
            observeStreakUseCase()
        ) { user, streak ->
            _uiState.update { state ->
                state.copy(
                    xp = user?.experiencePoints ?: 0,
                    createdAtText = formatCreatedAt(user?.createdAt),
                    streakCount = streak?.lengthStreak ?: 0
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun formatCreatedAt(raw: String?): String {
        if (raw.isNullOrBlank()) return ""
        val datePart = raw.trim().take(10)
        val parts = datePart.split("-")
        if (parts.size != 3) return raw
        val yyyy = parts[0]
        val mm = parts[1]
        val dd = parts[2]
        return "$dd/$mm/$yyyy"
    }
}
