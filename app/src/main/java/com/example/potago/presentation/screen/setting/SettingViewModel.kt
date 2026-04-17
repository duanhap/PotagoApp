package com.example.potago.presentation.screen.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.repository.UserRepository
import com.example.potago.domain.usecase.LogoutWithFireBaseUseCase
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

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val logoutUseCase: LogoutWithFireBaseUseCase,
    private val userRepository: UserRepository
): ViewModel(){
    private val _uiState = MutableStateFlow(LogoutUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: LogoutEvent) {
        when (event) {
            is LogoutEvent.Submit -> logout()
            LogoutEvent.ErrorShown -> clearError()
        }
    }
    private fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(authState = UiState.Loading) }
            
            // 1. Đăng xuất Firebase
            logoutUseCase()
            
            // 2. Xóa sạch dữ liệu DataStore
            userRepository.clearUser()

            _uiState.update { it.copy(authState = UiState.Success()) }

            // 3. Gửi event điều hướng thoát ra khỏi MainGraph
            _uiEvent.send(
                UiEvent.Navigate(
                    route = Screen.AuthGraph.route,
                    popUpTo = Screen.MainGraph.route,
                    inclusive = true
                )
            )
        }
    }

    private fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
data class LogoutUiState(
    val authState: UiState<Unit> = UiState.Idle,
    val errorMessage: String? = null
)

sealed class LogoutEvent {
    object Submit : LogoutEvent()
    object ErrorShown : LogoutEvent()
}
