package com.example.potago.presentation.screen.listofcardsscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Word
import com.example.potago.domain.model.Result
import com.example.potago.domain.usecase.GetWordsByWordSetIdUseCase
import com.example.potago.domain.usecase.DeleteWordUseCase
import com.example.potago.domain.usecase.GetWordSetByIdUseCase
import com.example.potago.domain.usecase.UpdateWordStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class FilterType {
    ALL, LEARNING, LEARNED
}

data class ListOfCardsUiState(
    val cards: List<Word> = emptyList(),
    val filterType: FilterType = FilterType.ALL,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val termLanguageCode: String = ""
)

@HiltViewModel
class ListOfCardsViewModel @Inject constructor(
    private val getWordsByWordSetIdUseCase: GetWordsByWordSetIdUseCase,
    private val deleteWordUseCase: DeleteWordUseCase,
    private val getWordSetByIdUseCase: GetWordSetByIdUseCase,
    private val updateWordStatusUseCase: UpdateWordStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListOfCardsUiState())
    val uiState: StateFlow<ListOfCardsUiState> = _uiState.asStateFlow()

    private var currentWordSetId: Long = -1L

    fun loadCards(wordSetId: Long) {
        currentWordSetId = wordSetId
        viewModelScope.launch {
            // Lấy termLanguageCode từ wordset
            when (val result = getWordSetByIdUseCase(wordSetId)) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    termLanguageCode = result.data.termLanguageCode
                )
                else -> {}
            }
        }
        fetchCards(wordSetId, _uiState.value.filterType)
    }

    fun onFilterChange(type: FilterType) {
        if (_uiState.value.filterType == type) return
        _uiState.value = _uiState.value.copy(filterType = type)
        fetchCards(currentWordSetId, type)
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun deleteWord(wordId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = deleteWordUseCase(wordId)) {
                is Result.Success -> fetchCards(currentWordSetId, _uiState.value.filterType)
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.message
                )
                else -> {}
            }
        }
    }

    fun toggleWordStatus(wordId: Long, currentStatus: String) {
        val newStatus = if (currentStatus.equals("known", ignoreCase = true)) "unknown" else "known"
        viewModelScope.launch {
            when (updateWordStatusUseCase(wordId, newStatus)) {
                is Result.Success -> fetchCards(currentWordSetId, _uiState.value.filterType)
                else -> {}
            }
        }
    }

    private fun fetchCards(wordSetId: Long, filterType: FilterType) {
        if (wordSetId == -1L) return
        val statusParam = when (filterType) {
            FilterType.ALL -> null
            FilterType.LEARNING -> "unknown"
            FilterType.LEARNED -> "known"
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = getWordsByWordSetIdUseCase(wordSetId, statusParam)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        cards = result.data,
                        isLoading = false
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                else -> {}
            }
        }
    }
}
