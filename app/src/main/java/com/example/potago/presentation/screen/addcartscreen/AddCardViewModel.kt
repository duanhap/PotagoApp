package com.example.potago.presentation.screen.addcartscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Result
import com.example.potago.domain.usecase.AddWordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddCardUiState(
    val term: String = "",
    val definition: String = "",
    val description: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class AddCardEvent {
    object NavigateBack : AddCardEvent()
    data class ShowError(val message: String) : AddCardEvent()
}

@HiltViewModel
class AddCardViewModel @Inject constructor(
    private val addWordUseCase: AddWordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddCardUiState())
    val uiState: StateFlow<AddCardUiState> = _uiState.asStateFlow()

    private val _events = Channel<AddCardEvent>()
    val events = _events.receiveAsFlow()

    fun onTermChange(term: String) {
        _uiState.update { it.copy(term = term, error = null) }
    }

    fun onDefinitionChange(definition: String) {
        _uiState.update { it.copy(definition = definition, error = null) }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun saveCard(wordSetId: Long) {
        val state = _uiState.value

        if (state.term.isBlank()) {
            viewModelScope.launch {
                _events.send(AddCardEvent.ShowError("Vui lòng nhập thuật ngữ"))
            }
            return
        }
        if (state.definition.isBlank()) {
            viewModelScope.launch {
                _events.send(AddCardEvent.ShowError("Vui lòng nhập định nghĩa"))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = addWordUseCase(
                wordSetId = wordSetId,
                term = state.term.trim(),
                definition = state.definition.trim(),
                description = state.description.trim().takeIf { it.isNotBlank() }
            )) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(AddCardEvent.NavigateBack)
                }
                is Result.Error -> {
                    val msg = result.message ?: "Thêm thẻ thất bại"
                    _uiState.update { it.copy(isLoading = false, error = msg) }
                    _events.send(AddCardEvent.ShowError(msg))
                }
                else -> _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
