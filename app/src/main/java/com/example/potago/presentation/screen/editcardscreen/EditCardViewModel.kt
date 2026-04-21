package com.example.potago.presentation.screen.editcardscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.usecase.UpdateWordUseCase
import com.example.potago.domain.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditCardUiState(
    val cardId: Long = 0L,
    val term: String = "",
    val definition: String = "",
    val description: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class EditCardViewModel @Inject constructor(
    private val updateWordUseCase: UpdateWordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditCardUiState())
    val uiState: StateFlow<EditCardUiState> = _uiState.asStateFlow()

    fun loadCard(cardId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, cardId = cardId) }
            
            // Temporary mock data for preview since there is no GET word endpoint requested
            _uiState.update {
                it.copy(
                    term = "El perro",
                    definition = "The dog",
                    description = "Los gatos que duermen en pantalones cortos suelen ser muy adorables.",
                    isLoading = false
                )
            }
        }
    }

    fun onTermChange(term: String) {
        _uiState.update { it.copy(term = term) }
    }

    fun onDefinitionChange(definition: String) {
        _uiState.update { it.copy(definition = definition) }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun saveCard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val currentState = _uiState.value
            when (val result = updateWordUseCase(
                wordId = currentState.cardId,
                term = currentState.term,
                definition = currentState.definition,
                description = currentState.description,
                status = "unknown"
            )) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                else -> {}
            }
        }
    }
}
