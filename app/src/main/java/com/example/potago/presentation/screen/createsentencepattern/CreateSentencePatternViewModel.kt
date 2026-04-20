package com.example.potago.presentation.screen.createsentencepattern

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Result
import com.example.potago.domain.repository.SentencePatternRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SentenceInput(
    val id: Int,
    val term: String = "",
    val definition: String = ""
)

data class CreateSentencePatternUiState(
    val title: String = "",
    val description: String = "",
    val termLangCode: String = "en",
    val defLangCode: String = "vi",
    val sentences: List<SentenceInput> = listOf(SentenceInput(0), SentenceInput(1)),
    val isSaving: Boolean = false,
    val showSaveConfirm: Boolean = false,
    val showCancelConfirm: Boolean = false
)

sealed class CreateSentencePatternEvent {
    object NavigateBack : CreateSentencePatternEvent()
    data class ShowError(val message: String) : CreateSentencePatternEvent()
}

@HiltViewModel
class CreateSentencePatternViewModel @Inject constructor(
    private val repository: SentencePatternRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateSentencePatternUiState())
    val uiState: StateFlow<CreateSentencePatternUiState> = _uiState.asStateFlow()

    private val _events = Channel<CreateSentencePatternEvent>()
    val events = _events.receiveAsFlow()

    private var nextId = 2

    fun onTitleChange(value: String) = _uiState.update { it.copy(title = value) }
    fun onDescriptionChange(value: String) = _uiState.update { it.copy(description = value) }
    fun onTermLangChange(value: String) = _uiState.update { it.copy(termLangCode = value) }
    fun onDefLangChange(value: String) = _uiState.update { it.copy(defLangCode = value) }

    fun onSentenceTermChange(id: Int, value: String) {
        _uiState.update { state ->
            state.copy(sentences = state.sentences.map {
                if (it.id == id) it.copy(term = value) else it
            })
        }
    }

    fun onSentenceDefinitionChange(id: Int, value: String) {
        _uiState.update { state ->
            state.copy(sentences = state.sentences.map {
                if (it.id == id) it.copy(definition = value) else it
            })
        }
    }

    fun addSentence() {
        _uiState.update { state ->
            state.copy(sentences = state.sentences + SentenceInput(nextId++))
        }
    }

    fun deleteOrClearSentence(id: Int) {
        val sentences = _uiState.value.sentences
        if (sentences.size <= 2) {
            _uiState.update { state ->
                state.copy(sentences = state.sentences.map {
                    if (it.id == id) it.copy(term = "", definition = "") else it
                })
            }
        } else {
            _uiState.update { state ->
                state.copy(sentences = state.sentences.filter { it.id != id })
            }
        }
    }

    fun showSaveConfirm() = _uiState.update { it.copy(showSaveConfirm = true) }
    fun dismissSaveConfirm() = _uiState.update { it.copy(showSaveConfirm = false) }
    fun showCancelConfirm() = _uiState.update { it.copy(showCancelConfirm = true) }
    fun dismissCancelConfirm() = _uiState.update { it.copy(showCancelConfirm = false) }

    fun save() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            viewModelScope.launch { _events.send(CreateSentencePatternEvent.ShowError("Vui lòng nhập tiêu đề")) }
            return
        }
        if (state.description.isBlank()) {
            viewModelScope.launch { _events.send(CreateSentencePatternEvent.ShowError("Vui lòng nhập mô tả")) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val pairs = state.sentences.map { Pair(it.term, it.definition) }
            when (val result = repository.createSentencePatternWithSentences(
                name = state.title,
                description = state.description,
                termLangCode = state.termLangCode,
                defLangCode = state.defLangCode,
                sentences = pairs
            )) {
                is Result.Success -> {
                    _uiState.update { it.copy(isSaving = false) }
                    _events.send(CreateSentencePatternEvent.NavigateBack)
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isSaving = false) }
                    _events.send(CreateSentencePatternEvent.ShowError(result.message ?: "Lỗi"))
                }
                else -> _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}
