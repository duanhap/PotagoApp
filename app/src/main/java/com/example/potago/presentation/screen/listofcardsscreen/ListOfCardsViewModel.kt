package com.example.potago.presentation.screen.listofcardsscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Word
import com.example.potago.domain.model.Result
import com.example.potago.domain.usecase.GetWordsByWordSetIdUseCase
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
    val error: String? = null
)

@HiltViewModel
class ListOfCardsViewModel @Inject constructor(
    private val getWordsByWordSetIdUseCase: GetWordsByWordSetIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListOfCardsUiState())
    val uiState: StateFlow<ListOfCardsUiState> = _uiState.asStateFlow()

    private var currentWordSetId: Long = -1L

    fun loadCards(wordSetId: Long) {
        currentWordSetId = wordSetId
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
