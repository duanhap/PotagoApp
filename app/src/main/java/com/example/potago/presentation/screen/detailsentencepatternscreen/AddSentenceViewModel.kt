package com.example.potago.presentation.screen.detailsentencepatternscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Setence
import com.example.potago.domain.usecase.CreateSentenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddSentenceUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val createSuccess: Boolean = false
)

@HiltViewModel
class AddSentenceViewModel @Inject constructor(
    private val createSentenceUseCase: CreateSentenceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddSentenceUiState())
    val uiState: StateFlow<AddSentenceUiState> = _uiState.asStateFlow()

    fun createSentence(
        patternId: Int,
        term: String,
        definition: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, createSuccess = false) }
            when (val result = createSentenceUseCase(
                patternId = patternId,
                term = term.trim(),
                definition = definition.trim(),
                status = "unknown",
                mistakes = 0
            )) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, createSuccess = true) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message ?: "Tạo câu thất bại") }
                }
                else -> {}
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
