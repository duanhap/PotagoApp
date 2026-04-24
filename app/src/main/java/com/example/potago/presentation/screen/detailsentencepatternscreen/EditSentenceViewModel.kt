package com.example.potago.presentation.screen.detailsentencepatternscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Setence
import com.example.potago.domain.usecase.GetSentenceByIdUseCase
import com.example.potago.domain.usecase.UpdateSentenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditSentenceUiState(
    val isLoading: Boolean = false,
    val sentence: Setence? = null,
    val error: String? = null,
    val updateSuccess: Boolean = false
)

@HiltViewModel
class EditSentenceViewModel @Inject constructor(
    private val getSentenceByIdUseCase: GetSentenceByIdUseCase,
    private val updateSentenceUseCase: UpdateSentenceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditSentenceUiState())
    val uiState: StateFlow<EditSentenceUiState> = _uiState.asStateFlow()

    fun loadSentence(sentenceId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = getSentenceByIdUseCase(sentenceId)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, sentence = result.data) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message ?: "Lỗi tải dữ liệu") }
                }
                else -> {}
            }
        }
    }

    fun updateSentence(
        sentenceId: Int,
        term: String,
        definition: String,
        status: String = "unknown",
        mistakes: Int = 0
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, updateSuccess = false) }
            when (val result = updateSentenceUseCase(
                id = sentenceId,
                term = term.trim(),
                definition = definition.trim(),
                status = status,
                mistakes = mistakes
            )) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, updateSuccess = true, sentence = result.data) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message ?: "Cập nhật câu thất bại") }
                }
                else -> {}
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
