package com.example.potago.presentation.screen.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.usecase.CheckAndExpireItemSessionUseCase
import com.example.potago.domain.usecase.GetCurrentUserWithFireBaseUseCase
import com.example.potago.domain.usecase.SyncUserSessionUseCase
import com.example.potago.presentation.screen.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserWithFireBaseUseCase,
    private val syncUserSessionUseCase: SyncUserSessionUseCase,
    private val checkAndExpireItemSessionUseCase: CheckAndExpireItemSessionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState = _uiState.asStateFlow()

    init {
        checkLogin()
    }

    private fun checkLogin() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(status = UiState.Loading)
            try {
                val user = getCurrentUserUseCase()
                if (user != null) {
                    // Đã login → sync DataStore + check item session hết hạn chưa
                    syncUserSessionUseCase()
                    checkAndExpireItemSessionUseCase()
                    _uiState.value = _uiState.value.copy(status = UiState.Success(), loggedIn = true)
                } else {
                    _uiState.value = _uiState.value.copy(status = UiState.Success(), loggedIn = false)
                }
            } catch (ex: Exception) {
                Log.d("SplashViewModel", ex.message ?: "Error")
                _uiState.value = _uiState.value.copy(
                    status = UiState.Error(ex.message ?: "Error"),
                    loggedIn = false
                )
            }
        }
    }

    fun reset() {
        _uiState.value = SplashUiState()
    }
}

data class SplashUiState(
    val status: UiState<Nothing> = UiState.Idle,
    val loggedIn: Boolean = false
)
