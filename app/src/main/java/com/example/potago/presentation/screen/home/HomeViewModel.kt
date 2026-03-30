package com.example.potago.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Setence
import com.example.potago.domain.model.WordSet
import com.example.potago.domain.usecase.GetRecentSentencesUseCase
import com.example.potago.domain.usecase.GetRecentWordSetsUseCase
import com.example.potago.presentation.screen.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRecentWordSetsUseCase: GetRecentWordSetsUseCase,
    private val getRecentSentencesUseCase: GetRecentSentencesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        loadRecentWordSets()
        loadRecentSentences()
    }

    private fun loadRecentWordSets() {
        viewModelScope.launch {
            _uiState.update { it.copy(recentWordSets = UiState.Loading) }
            when (val result = getRecentWordSetsUseCase(limit = 3)) {
                is Result.Success -> {
                    _uiState.update { it.copy(recentWordSets = UiState.Success(result.data)) }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(recentWordSets = UiState.Error(result.message ?: "Lỗi không xác định"))
                    }
                }
                else -> {}
            }
        }
    }

    private fun loadRecentSentences() {
        viewModelScope.launch {
            _uiState.update { it.copy(recentSentences = UiState.Loading) }
            when (val result = getRecentSentencesUseCase(limit = 3)) {
                is Result.Success -> {
                    _uiState.update { it.copy(recentSentences = UiState.Success(result.data)) }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(recentSentences = UiState.Error(result.message ?: "Lỗi không xác định"))
                    }
                }
                else -> {}
            }
        }
    }
}

data class HomeUiState(
    val recentWordSets: UiState<List<WordSet>> = UiState.Loading,
    val recentSentences: UiState<List<Setence>> = UiState.Loading
)
