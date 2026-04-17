package com.example.potago.presentation.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.User
import com.example.potago.domain.usecase.GetUserProfileUseCase
import com.example.potago.domain.repository.UserRepository
import com.example.potago.presentation.screen.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: UiState<User> = UiState.Loading,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isSaving: Boolean = false
)

sealed class ProfileEvent {
    data class ShowSnackbar(val message: String) : ProfileEvent()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _events = Channel<ProfileEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            // Load from local cache first
            userRepository.getSavedUser().collect { cachedUser ->
                if (cachedUser != null) {
                    _uiState.update {
                        it.copy(
                            user = UiState.Success(cachedUser),
                            name = cachedUser.name,
                            email = cachedUser.email ?: ""
                        )
                    }
                }
            }
        }
        viewModelScope.launch {
            when (val result = getUserProfileUseCase()) {
                is Result.Success -> {
                    val user = result.data!!
                    _uiState.update {
                        it.copy(
                            user = UiState.Success(user),
                            name = user.name,
                            email = user.email ?: ""
                        )
                    }
                }
                is Result.Error -> {
                    if (_uiState.value.user is UiState.Loading) {
                        _uiState.update { it.copy(user = UiState.Error(result.message ?: "Lỗi")) }
                    }
                }
                else -> {}
            }
        }
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value) }

    fun saveProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            // TODO: call update profile API when endpoint is available
            kotlinx.coroutines.delay(800)
            _uiState.update { it.copy(isSaving = false) }
            _events.send(ProfileEvent.ShowSnackbar("Lưu thành công!"))
        }
    }
}
