package com.example.potago.presentation.screen.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Result
import com.example.potago.domain.repository.UserRepository
import com.example.potago.domain.usecase.GetUserSettingsUseCase
import com.example.potago.domain.usecase.LogoutWithFireBaseUseCase
import com.example.potago.domain.usecase.UpdateUserSettingsUseCase
import com.example.potago.presentation.navigation.Screen
import com.example.potago.presentation.screen.UiEvent
import com.example.potago.presentation.screen.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingUiState(
    val notification: Boolean = false,
    val language: String = "vi",
    val experienceGoal: Int = 15,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val authState: UiState<Unit> = UiState.Idle,
    val errorMessage: String? = null
)

sealed class LogoutEvent {
    object Submit : LogoutEvent()
    object ErrorShown : LogoutEvent()
}

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val logoutUseCase: LogoutWithFireBaseUseCase,
    private val userRepository: UserRepository,
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val updateUserSettingsUseCase: UpdateUserSettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getUserSettingsUseCase()) {
                is Result.Success -> {
                    val s = result.data!!
                    _uiState.update {
                        it.copy(
                            notification = s.notification,
                            language = s.language ?: "vi",
                            experienceGoal = s.experienceGoal,
                            isLoading = false
                        )
                    }
                }
                else -> _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onNotificationChange(value: Boolean) {
        _uiState.update { it.copy(notification = value) }
        saveSettings(_uiState.value)
    }

    fun onLanguageChange(value: String) {
        _uiState.update { it.copy(language = value) }
        saveSettings(_uiState.value)
    }

    private fun saveSettings(state: SettingUiState = _uiState.value) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            updateUserSettingsUseCase(
                notification = state.notification,
                language = state.language,
                experienceGoal = state.experienceGoal
            )
            _uiState.update { it.copy(isSaving = false) }
        }
    }

    fun onEvent(event: LogoutEvent) {
        when (event) {
            is LogoutEvent.Submit -> logout()
            LogoutEvent.ErrorShown -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(authState = UiState.Loading) }
            logoutUseCase()
            userRepository.clearUser()
            _uiState.update { it.copy(authState = UiState.Success()) }
            _uiEvent.send(
                UiEvent.Navigate(
                    route = Screen.AuthGraph.route,
                    popUpTo = Screen.MainGraph.route,
                    inclusive = true
                )
            )
        }
    }
}
