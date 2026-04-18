package com.example.potago.presentation.screen.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.presentation.navigation.Screen
import com.example.potago.domain.model.User
import com.example.potago.domain.model.Result
import com.example.potago.domain.usecase.LoginWithFireBaseUseCase
import com.example.potago.domain.usecase.ObserveUserUseCase
import com.example.potago.domain.usecase.SyncUserSessionUseCase
import com.example.potago.presentation.screen.UiEvent
import com.example.potago.presentation.screen.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginWithFireBaseUseCase,
    private val syncUserSessionUseCase: SyncUserSessionUseCase,
    private val observeUserUseCase: ObserveUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        // Tự động điền email từ cache nếu có
        viewModelScope.launch {
            observeUserUseCase().first()?.let { savedUser ->
                updateState { it.copy(email = savedUser.email ?: "") }
            }
        }
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> updateState { it.copy(email = event.value) }
            is LoginEvent.PasswordChanged -> updateState { it.copy(password = event.value) }
            LoginEvent.TogglePasswordVisibility -> updateState { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            LoginEvent.Submit -> login()
            LoginEvent.ErrorShown -> updateState { it.copy(errorMessage = null) }
        }
    }

    private fun updateState(update: (LoginUiState) -> LoginUiState) {
        _uiState.update {
            val newState = update(it)
            newState.copy(
                isLoginButtonEnabled = newState.email.isNotBlank() && newState.password.isNotBlank()
            )
        }
    }

    private fun login() {
        val state = _uiState.value
        if (state.authState is UiState.Loading) return

        viewModelScope.launch {
            _uiState.update { it.copy(authState = UiState.Loading, errorMessage = null) }
            try {
                // 1. Đăng nhập Firebase
                loginUseCase(state.email.trim(), state.password)
                delay(1000)

                // 2. Fetch & lưu toàn bộ session (profile, settings, streak, today streak)
                when (val result = syncUserSessionUseCase()) {
                    is Result.Success -> {
                        _uiState.update { it.copy(authState = UiState.Success(Unit)) }
                        _uiEvent.send(UiEvent.Navigate(
                            route = Screen.MainGraph.route,
                            popUpTo = Screen.Login.route,
                            inclusive = true
                        ))
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(authState = UiState.Error(result.message), errorMessage = result.message) }
                        _uiEvent.send(UiEvent.ShowSnackbar(result.message))
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Đăng nhập thất bại"
                _uiState.update { it.copy(authState = UiState.Error(errorMsg), errorMessage = errorMsg) }
                _uiEvent.send(UiEvent.ShowSnackbar(errorMsg))
            }
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoginButtonEnabled: Boolean = false,
    val authState: UiState<Any> = UiState.Idle,
    val errorMessage: String? = null
)

sealed class LoginEvent {
    data class EmailChanged(val value: String) : LoginEvent()
    data class PasswordChanged(val value: String) : LoginEvent()
    object TogglePasswordVisibility : LoginEvent()
    object Submit : LoginEvent()
    object ErrorShown : LoginEvent()
}
