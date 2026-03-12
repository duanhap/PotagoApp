package com.example.potago.presentation.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Result
import com.example.potago.presentation.navigation.Screen
import com.example.potago.domain.model.User
import com.example.potago.domain.usecase.RegisterBackendUseCase
import com.example.potago.domain.usecase.RegisterWithFireBaseUseCase
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
class SignUpViewModel @Inject constructor(
    private val registerFirebaseUseCase: RegisterWithFireBaseUseCase,
    private val registerBackendUseCase: RegisterBackendUseCase,
): ViewModel(){
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.NameChanged -> updateState { it.copy(name = event.value) }
            is SignUpEvent.EmailChanged -> updateState { it.copy(email = event.value) }
            is SignUpEvent.PasswordChanged -> updateState { it.copy(password = event.value) }
            is SignUpEvent.ConfirmPasswordChanged -> updateState { it.copy(confirmPassword = event.value) }
            SignUpEvent.TogglePasswordVisibility -> updateState { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            SignUpEvent.ToggleConfirmPasswordVisibility -> updateState { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
            SignUpEvent.Submit -> createAccount()
            SignUpEvent.ErrorShown -> updateState { it.copy(errorMessage = null) }
        }
    }

    private fun updateState(update: (SignUpUiState) -> SignUpUiState) {
        _uiState.update { 
            val newState = update(it)
            newState.copy(
                isCreateAccountButtonEnabled = newState.name.isNotBlank() && 
                        newState.email.isNotBlank() && 
                        newState.password.isNotBlank() && 
                        newState.confirmPassword.isNotBlank()
            )
        }
    }

    private fun createAccount() {
        val state = _uiState.value
        if (state.password != state.confirmPassword) {
            _uiEvent.trySend(UiEvent.ShowSnackbar("Passwords do not match"))
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(authState = UiState.Loading) }
            
            try {
                // Bước 1: Đăng ký Firebase
               registerFirebaseUseCase(state.email, state.password)
                
                // Bước 2: Đăng ký Backend (DB)
                val backendResult = registerBackendUseCase(state.email, state.name)
                
                when (backendResult) {
                    is Result.Success -> {
                        _uiState.update { it.copy(authState = UiState.Success(backendResult.data)) }
                        _uiEvent.send(UiEvent.ShowSnackbar("Đăng ký thành công!"))
                        _uiEvent.send(UiEvent.Navigate(route = Screen.Login.route, popUpTo = Screen.SignUp.route, inclusive = true))
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(authState = UiState.Error(backendResult.message), errorMessage = backendResult.message) }
                        _uiEvent.send(UiEvent.ShowSnackbar(backendResult.message))
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Đăng ký thất bại"
                _uiState.update { it.copy(authState = UiState.Error(errorMsg), errorMessage = errorMsg) }
                _uiEvent.send(UiEvent.ShowSnackbar(errorMsg))
            }
        }
    }
}

data class SignUpUiState(
    val name : String ="",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isCreateAccountButtonEnabled: Boolean = false,
    val authState: UiState<User> = UiState.Idle,
    val errorMessage: String? = null
)

sealed class SignUpEvent {
    data class NameChanged(val value: String) : SignUpEvent()
    data class EmailChanged(val value: String) : SignUpEvent()
    data class PasswordChanged(val value: String) : SignUpEvent()
    data class ConfirmPasswordChanged(val value: String) : SignUpEvent()
    object TogglePasswordVisibility : SignUpEvent()
    object ToggleConfirmPasswordVisibility : SignUpEvent()
    object Submit : SignUpEvent()
    object ErrorShown : SignUpEvent()
}
