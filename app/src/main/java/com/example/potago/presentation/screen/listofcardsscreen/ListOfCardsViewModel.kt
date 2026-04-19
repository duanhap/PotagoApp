package com.example.potago.presentation.screen.listofcardsscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Word
import com.example.potago.domain.model.Result
import com.example.potago.domain.usecase.GetFlashcardsUseCase
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
    private val getFlashcardsUseCase: GetFlashcardsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListOfCardsUiState())
    val uiState: StateFlow<ListOfCardsUiState> = _uiState.asStateFlow()

    private var currentWordSetId: Long = 0L

    fun loadCards(wordSetId: Long) {
        currentWordSetId = wordSetId
        if (_uiState.value.cards.isNotEmpty()) return // Already loaded

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            // Lấy tất cả card (có thể thay đổi endpoint/hàm nếu có getWordsByWordSetId)
            when (val result = getFlashcardsUseCase(wordSetId, "REVIEW", size = 100)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        cards = result.data.words,
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

    fun onFilterChange(type: FilterType) {
        _uiState.value = _uiState.value.copy(filterType = type)
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }
}
