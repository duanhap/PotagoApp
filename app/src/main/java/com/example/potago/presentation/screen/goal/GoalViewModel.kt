package com.example.potago.presentation.screen.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Result
import com.example.potago.domain.usecase.GetUserSettingsUseCase
import com.example.potago.domain.usecase.UpdateUserSettingsUseCase
import com.example.potago.presentation.navigation.Screen
import com.example.potago.presentation.screen.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

data class GoalUiState(
    val selectedXp: Int? = null,
    val isLoadingSettings: Boolean = true,
    val isSaving: Boolean = false
)

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val updateUserSettingsUseCase: UpdateUserSettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        // Khi vừa vào màn Goal, lấy goal hiện tại để dropdown hiển thị đúng.
        viewModelScope.launch {
            when (val settingsResult = getUserSettingsUseCase()) {
                is Result.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            selectedXp = settingsResult.data.experienceGoal,
                            isLoadingSettings = false
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            selectedXp = null,
                            isLoadingSettings = false
                        )
                    }
                }
                else -> {
                    _uiState.update { state ->
                        state.copy(isLoadingSettings = false)
                    }
                }
            }
        }
    }

    fun onSaveGoal(experienceGoal: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)

            try {
                // Backend yêu cầu body có notification + language,
                // nên lấy settings hiện tại để không ghi đè lung tung.
                val currentSettings = when (val settingsResult = getUserSettingsUseCase()) {
                    is Result.Success -> settingsResult.data
                    is Result.Error -> null
                    else -> null
                }

                val notification = currentSettings?.notification ?: false
                val language = (currentSettings?.language ?: "en").ifBlank { "en" }

                when (val updateResult = updateUserSettingsUseCase(
                    notification = notification,
                    language = language,
                    experienceGoal = experienceGoal
                )) {
                    is Result.Success -> {
                        _uiEvent.send(
                            UiEvent.Navigate(
                                route = Screen.Potato.route,
                                popUpTo = Screen.Goal.route,
                                inclusive = true
                            )
                        )
                    }
                    is Result.Error -> {
                        _uiEvent.send(UiEvent.ShowSnackbar(updateResult.message))
                    }
                    else -> {}
                }
            } finally {
                _uiState.value = _uiState.value.copy(isSaving = false)
            }
        }
    }
}

