package com.example.potago.presentation.screen.createwordset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Result
import com.example.potago.domain.repository.WordSetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CardInput(
    val id: Int,
    val term: String = "",
    val definition: String = "",
    val description: String = ""
)

data class CreateWordSetUiState(
    val title: String = "",
    val description: String = "",
    val termLangCode: String = "en",
    val defLangCode: String = "vi",
    val cards: List<CardInput> = listOf(CardInput(0), CardInput(1)),
    val isSaving: Boolean = false,
    val showSaveConfirm: Boolean = false,
    val showCancelConfirm: Boolean = false
)

sealed class CreateWordSetEvent {
    object NavigateBack : CreateWordSetEvent()
    data class ShowError(val message: String) : CreateWordSetEvent()
}

@HiltViewModel
class CreateWordSetViewModel @Inject constructor(
    private val wordSetRepository: WordSetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateWordSetUiState())
    val uiState: StateFlow<CreateWordSetUiState> = _uiState.asStateFlow()

    private val _events = Channel<CreateWordSetEvent>()
    val events = _events.receiveAsFlow()

    private var nextCardId = 2

    fun onNameChange(value: String) = _uiState.update { it.copy(title = value) }
    fun onTitleChange(value: String) = _uiState.update { it.copy(title = value) }
    fun showSaveConfirm() = _uiState.update { it.copy(showSaveConfirm = true) }
    fun dismissSaveConfirm() = _uiState.update { it.copy(showSaveConfirm = false) }
    fun showCancelConfirm() = _uiState.update { it.copy(showCancelConfirm = true) }
    fun dismissCancelConfirm() = _uiState.update { it.copy(showCancelConfirm = false) }
    fun onDescriptionChange(value: String) = _uiState.update { it.copy(description = value) }
    fun onTermLangChange(value: String) = _uiState.update { it.copy(termLangCode = value) }
    fun onDefLangChange(value: String) = _uiState.update { it.copy(defLangCode = value) }

    fun onCardTermChange(cardId: Int, value: String) {
        _uiState.update { state ->
            state.copy(cards = state.cards.map {
                if (it.id == cardId) it.copy(term = value) else it
            })
        }
    }

    fun onCardDefinitionChange(cardId: Int, value: String) {
        _uiState.update { state ->
            state.copy(cards = state.cards.map {
                if (it.id == cardId) it.copy(definition = value) else it
            })
        }
    }

    fun onCardDescriptionChange(cardId: Int, value: String) {
        _uiState.update { state ->
            state.copy(cards = state.cards.map {
                if (it.id == cardId) it.copy(description = value) else it
            })
        }
    }

    fun addCard() {
        _uiState.update { state ->
            state.copy(cards = state.cards + CardInput(nextCardId++))
        }
    }

    fun deleteOrClearCard(cardId: Int) {
        val cards = _uiState.value.cards
        if (cards.size <= 2) {
            // Reset content only
            _uiState.update { state ->
                state.copy(cards = state.cards.map {
                    if (it.id == cardId) it.copy(term = "", definition = "", description = "") else it
                })
            }
        } else {
            _uiState.update { state ->
                state.copy(cards = state.cards.filter { it.id != cardId })
            }
        }
    }

    fun save() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            viewModelScope.launch { _events.send(CreateWordSetEvent.ShowError("Vui lòng nhập tiêu đề")) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val words = state.cards.map { Pair(it.term, it.definition) }
            when (val result = wordSetRepository.createWordSetWithWords(
                name = state.title,
                description = state.description.takeIf { it.isNotBlank() },
                termLangCode = state.termLangCode,
                defLangCode = state.defLangCode,
                words = words
            )) {
                is Result.Success -> {
                    _uiState.update { it.copy(isSaving = false) }
                    _events.send(CreateWordSetEvent.NavigateBack)
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isSaving = false) }
                    _events.send(CreateWordSetEvent.ShowError(result.message ?: "Lỗi"))
                }
                else -> _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}
