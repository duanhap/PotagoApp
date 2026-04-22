package com.example.potago.presentation.screen.detailsentencepatternscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Setence
import com.example.potago.domain.model.SetencePattern
import com.example.potago.domain.usecase.DeleteSentencePatternUseCase
import com.example.potago.domain.usecase.GetSentencePatternDetailsUseCase
import com.example.potago.domain.usecase.UpdateSentencePatternUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailSentencePatternUiState(
    val isLoading: Boolean = false,
    val pattern: SetencePattern? = null,
    val sentences: List<Setence> = emptyList(),
    val error: String? = null,
    val isUpdating: Boolean = false,
    val isDeleting: Boolean = false,
    val updateSuccess: Boolean = false,
    val deleteSuccess: Boolean = false,
    val actionError: String? = null
)

@HiltViewModel
class DetailSentencePatternViewModel @Inject constructor(
    private val getSentencePatternDetailsUseCase: GetSentencePatternDetailsUseCase,
    private val updateSentencePatternUseCase: UpdateSentencePatternUseCase,
    private val deleteSentencePatternUseCase: DeleteSentencePatternUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailSentencePatternUiState())
    val uiState: StateFlow<DetailSentencePatternUiState> = _uiState.asStateFlow()

    fun loadDetail(patternId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = getSentencePatternDetailsUseCase(patternId)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            pattern = result.data.pattern,
                            sentences = result.data.sentences
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message ?: "An error occurred"
                        )
                    }
                }
                else -> {}
            }
        }
    }

    fun updatePattern(
        name: String,
        description: String,
        termLangCode: String,
        defLangCode: String,
        isPublic: Boolean
    ) {
        val patternId = _uiState.value.pattern?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, actionError = null, updateSuccess = false) }
            when (val result = updateSentencePatternUseCase(
                id = patternId,
                name = name,
                description = description,
                termLangCode = termLangCode,
                defLangCode = defLangCode,
                isPublic = isPublic
            )) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            pattern = result.data,
                            updateSuccess = true
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            actionError = result.message ?: "Cập nhật thất bại"
                        )
                    }
                }
                else -> {}
            }
        }
    }

    fun deletePattern() {
        val patternId = _uiState.value.pattern?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, actionError = null, deleteSuccess = false) }
            when (val result = deleteSentencePatternUseCase(patternId)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isDeleting = false, deleteSuccess = true) }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            actionError = result.message ?: "Xóa thất bại"
                        )
                    }
                }
                else -> {}
            }
        }
    }

    fun clearActionError() {
        _uiState.update { it.copy(actionError = null) }
    }

    fun clearUpdateSuccess() {
        _uiState.update { it.copy(updateSuccess = false) }
    }
}
