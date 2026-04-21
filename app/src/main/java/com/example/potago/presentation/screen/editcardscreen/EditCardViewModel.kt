package com.example.potago.presentation.screen.editcardscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    // TODO: Inject repository when available
    // private val wordSetRepository: WordSetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditCardUiState())
    val uiState: StateFlow<EditCardUiState> = _uiState.asStateFlow()

    fun loadCard(cardId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, cardId = cardId) }
            
            // TODO: Load card data from repository
            // Example:
            // when (val result = wordSetRepository.getCard(cardId)) {
            //     is Result.Success -> {
            //         _uiState.update {
            //             it.copy(
            //                 term = result.data.term,
            //                 definition = result.data.definition,
            //                 description = result.data.description ?: "",
            //                 isLoading = false
            //             )
            //         }
            //     }
            //     is Result.Error -> {
            //         _uiState.update {
            //             it.copy(
            //                 isLoading = false,
            //                 error = result.message
            //             )
            //         }
            //     }
            // }
            
            // Temporary mock data for preview
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
            _uiState.update { it.copy(isLoading = true) }
            
            // TODO: Save card data to repository
            // Example:
            // val updatedCard = Card(
            //     id = _uiState.value.cardId,
            //     term = _uiState.value.term,
            //     definition = _uiState.value.definition,
            //     description = _uiState.value.description
            // )
            // when (val result = wordSetRepository.updateCard(updatedCard)) {
            //     is Result.Success -> {
            //         _uiState.update {
            //             it.copy(
            //                 isLoading = false,
            //                 isSuccess = true
            //             )
            //         }
            //     }
            //     is Result.Error -> {
            //         _uiState.update {
            //             it.copy(
            //                 isLoading = false,
            //                 error = result.message
            //             )
            //         }
            //     }
            // }
            
            // Temporary mock success
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isSuccess = true
                )
            }
        }
    }
}
