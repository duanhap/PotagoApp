package com.example.potago.presentation.screen.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.User
import com.example.potago.domain.repository.UserRepository
import com.example.potago.domain.usecase.GetUserProfileUseCase
import com.example.potago.domain.usecase.UpdateUserProfileUseCase
import com.example.potago.domain.usecase.UploadAvatarUseCase
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
    val isSaving: Boolean = false,
    val isUploadingAvatar: Boolean = false,
    val isSaveButtonEnabled: Boolean = false
)

sealed class ProfileEvent {
    data class ShowSnackbar(val message: String) : ProfileEvent()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val uploadAvatarUseCase: UploadAvatarUseCase,
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
        // Load from local cache first for instant display
        viewModelScope.launch {
            userRepository.getSavedUser().collect { cachedUser ->
                if (cachedUser != null && _uiState.value.user is UiState.Loading) {
                    _uiState.update {
                        it.copy(
                            user = UiState.Success(cachedUser),
                            name = cachedUser.name,
                            email = cachedUser.email ?: ""
                        )
                    }
                    updateSaveButtonState()
                }
            }
        }
        // Fetch fresh from API
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
                    updateSaveButtonState()
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

    private fun updateSaveButtonState() {
        val currentState = _uiState.value
        val originalName = (currentState.user as? UiState.Success)?.data?.name ?: ""
        val isNameChanged = currentState.name.isNotBlank() && currentState.name != originalName
        
        _uiState.update { it.copy(isSaveButtonEnabled = isNameChanged) }
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value) }
        updateSaveButtonState()
    }
    
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value) }

    fun onAvatarSelected(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingAvatar = true) }
            when (val result = uploadAvatarUseCase(uri)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isUploadingAvatar = false,
                            user = UiState.Success(result.data!!)
                        )
                    }
                    updateSaveButtonState()
                    _events.send(ProfileEvent.ShowSnackbar("Cập nhật ảnh thành công!"))
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isUploadingAvatar = false) }
                    _events.send(ProfileEvent.ShowSnackbar(result.message ?: "Upload thất bại"))
                }
                else -> _uiState.update { it.copy(isUploadingAvatar = false) }
            }
        }
    }

    fun saveProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val currentAvatar = (_uiState.value.user as? UiState.Success)?.data?.avatar
            when (val result = updateUserProfileUseCase(
                name = _uiState.value.name.takeIf { it.isNotBlank() },
                avatar = currentAvatar
            )) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            user = UiState.Success(result.data!!)
                        )
                    }
                    updateSaveButtonState()
                    _events.send(ProfileEvent.ShowSnackbar("Lưu thành công!"))
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isSaving = false) }
                    _events.send(ProfileEvent.ShowSnackbar(result.message ?: "Lưu thất bại"))
                }
                else -> _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}
