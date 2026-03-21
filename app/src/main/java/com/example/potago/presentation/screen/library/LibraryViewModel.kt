package com.example.potago.presentation.screen.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.WordSet
import com.example.potago.domain.usecase.GetRecentWordSetsUseCase
import com.example.potago.domain.usecase.GetWordSetsUseCase
import com.example.potago.presentation.screen.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getWordSetsUseCase: GetWordSetsUseCase,
    private val getRecentWordSetsUseCase: GetRecentWordSetsUseCase
) : ViewModel() {
    private val _recentWordSets = MutableStateFlow<UiState<List<WordSet>>>(UiState.Loading)
    val recentWordSets: StateFlow<UiState<List<WordSet>>> = _recentWordSets

    private val _allWordSets = MutableStateFlow<UiState<List<WordSet>>>(UiState.Loading)
    val allWordSets: StateFlow<UiState<List<WordSet>>> = _allWordSets

    init {
        refreshLibrary()
    }

    fun refreshLibrary() {
        loadRecentWordSets()
        loadAllWordSets()
    }

    private fun loadRecentWordSets() {
        viewModelScope.launch {
            _recentWordSets.value = UiState.Loading
            when (val result = getRecentWordSetsUseCase()) {
                is Result.Success -> _recentWordSets.value = UiState.Success(result.data)
                is Result.Error -> _recentWordSets.value = UiState.Error(result.message)
                else -> {}
            }
        }
    }

    private fun loadAllWordSets() {
        viewModelScope.launch {
            _allWordSets.value = UiState.Loading
            when (val result = getWordSetsUseCase()) {
                is Result.Success -> _allWordSets.value = UiState.Success(result.data)
                is Result.Error -> _allWordSets.value = UiState.Error(result.message)
                else -> {}
            }
        }
    }
}
