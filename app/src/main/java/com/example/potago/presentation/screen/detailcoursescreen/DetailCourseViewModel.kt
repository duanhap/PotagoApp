package com.example.potago.presentation.screen.detailcoursescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Result
import com.example.potago.domain.usecase.DeleteWordSetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailCourseUiState(
    val isLoading: Boolean = false,
    val isDeleted: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DetailCourseViewModel @Inject constructor(
    private val deleteWordSetUseCase: DeleteWordSetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailCourseUiState())
    val uiState: StateFlow<DetailCourseUiState> = _uiState.asStateFlow()

    fun deleteWordSet(wordSetId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = deleteWordSetUseCase(wordSetId)) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isDeleted = true
                )
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.message
                )
                else -> {}
            }
        }
    }
}
