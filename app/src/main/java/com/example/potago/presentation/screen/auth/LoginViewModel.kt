package com.example.potago.presentation.screen.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.presentation.navigation.Screen
import com.example.potago.domain.model.User
import com.example.potago.domain.model.Result
import com.example.potago.domain.repository.UserRepository
import com.example.potago.domain.usecase.GetUserProfileUseCase
import com.example.potago.domain.usecase.GetUserSettingsUseCase
import com.example.potago.domain.usecase.GetCurrentStreakUseCase
import com.example.potago.domain.usecase.GetTodayStreakDateUseCase
import com.example.potago.domain.usecase.LoginWithFireBaseUseCase
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
import com.example.potago.data.local.UserDataStore

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginWithFireBaseUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val getCurrentStreakUseCase: GetCurrentStreakUseCase,
    private val getTodayStreakDateUseCase: GetTodayStreakDateUseCase,
    private val userRepository: UserRepository,
    private val userDataStore: UserDataStore
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        // Tự động điền email từ DataStore nếu có (lấy từ lần đăng ký/đăng nhập trước)
        viewModelScope.launch {
            userRepository.getSavedUser().first()?.let { savedUser ->
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

                // 2. Lấy profile từ backend (Đã bao gồm saveUser trong Repository)
                val profileResult = getUserProfileUseCase()
                if (profileResult is Result.Success) {
                    
                    // 3. Lấy Settings và lưu vào DataStore
                    val settingsResult = getUserSettingsUseCase()
                    if (settingsResult is Result.Success && settingsResult.data != null) {
                        userDataStore.saveSetting(settingsResult.data)
                        Log.d("LoginViewModel", "Settings saved to DataStore: ${settingsResult.data}")
                    }

                    // 4. Lấy Streak và lưu vào DataStore
                    val streakResult = getCurrentStreakUseCase()
                    if (streakResult is Result.Success && streakResult.data != null) {
                        userDataStore.saveStreak(streakResult.data)
                        Log.d("LoginViewModel", "Streak saved to DataStore: ${streakResult.data}")
                    }

                    // 5. Lấy StreakDate hôm nay và lưu vào DataStore
                    val todayStreakResult = getTodayStreakDateUseCase()
                    if (todayStreakResult is Result.Success && todayStreakResult.data != null) {
                        userDataStore.saveTodayStreakDate(todayStreakResult.data)
                        Log.d("LoginViewModel", "TodayStreakDate saved to DataStore: ${todayStreakResult.data}")
                    }

                    _uiState.update { it.copy(authState = UiState.Success(profileResult.data)) }
                    _uiEvent.send(UiEvent.Navigate(
                        route = Screen.MainGraph.route,
                        popUpTo = Screen.Login.route,
                        inclusive = true
                    ))
                } else if (profileResult is Result.Error) {
                    _uiState.update { it.copy(authState = UiState.Error(profileResult.message), errorMessage = profileResult.message) }
                    _uiEvent.send(UiEvent.ShowSnackbar(profileResult.message))
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
    val authState: UiState<User> = UiState.Idle,
    val errorMessage: String? = null
)

sealed class LoginEvent {
    data class EmailChanged(val value: String) : LoginEvent()
    data class PasswordChanged(val value: String) : LoginEvent()
    object TogglePasswordVisibility : LoginEvent()
    object Submit : LoginEvent()
    object ErrorShown : LoginEvent()
}
