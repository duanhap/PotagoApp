package com.example.potago.presentation.screen.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.SetencePattern
import com.example.potago.domain.model.WordSet
import com.example.potago.domain.usecase.GetRecentWordSetsUseCase
import com.example.potago.domain.usecase.GetRecentSentencePatternsUseCase
import com.example.potago.domain.usecase.GetSentencePatternsUseCase
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
    private val getRecentWordSetsUseCase: GetRecentWordSetsUseCase,
    private val getSentencePatternsUseCase: GetSentencePatternsUseCase,
    private val getRecentSentencePatternsUseCase: GetRecentSentencePatternsUseCase
) : ViewModel() {
    private val _recentWordSets = MutableStateFlow<UiState<List<WordSet>>>(UiState.Loading)
    val recentWordSets: StateFlow<UiState<List<WordSet>>> = _recentWordSets

    private val _allWordSets = MutableStateFlow<UiState<List<WordSet>>>(UiState.Loading)
    val allWordSets: StateFlow<UiState<List<WordSet>>> = _allWordSets

    private val _recentSentencePatterns =
        MutableStateFlow<UiState<List<SetencePattern>>>(UiState.Loading)
    val recentSentencePatterns: StateFlow<UiState<List<SetencePattern>>> = _recentSentencePatterns

    private val _allSentencePatterns =
        MutableStateFlow<UiState<List<SetencePattern>>>(UiState.Loading)
    val allSentencePatterns: StateFlow<UiState<List<SetencePattern>>> = _allSentencePatterns

    init {
        refreshLibrary()
    }

    fun refreshLibrary() {
        loadRecentWordSets()
        loadAllWordSets()
        loadRecentSentencePatterns()
        loadAllSentencePatterns()
    }

    private fun loadRecentWordSets() {
        viewModelScope.launch {
            if (_recentWordSets.value !is UiState.Success) {
                _recentWordSets.value = UiState.Loading
            }
            when (val result = getRecentWordSetsUseCase()) {
                is Result.Success -> _recentWordSets.value = UiState.Success(result.data)
                is Result.Error -> _recentWordSets.value = UiState.Error(result.message)
                else -> {}
            }
        }
    }

    private fun loadAllWordSets() {
        viewModelScope.launch {
            if (_allWordSets.value !is UiState.Success) {
                _allWordSets.value = UiState.Loading
            }
            when (val result = getWordSetsUseCase()) {
                is Result.Success -> _allWordSets.value = UiState.Success(result.data)
                is Result.Error -> _allWordSets.value = UiState.Error(result.message)
                else -> {}
            }
        }
    }

    private fun loadRecentSentencePatterns() {
        viewModelScope.launch {
            if (_recentSentencePatterns.value !is UiState.Success) {
                _recentSentencePatterns.value = UiState.Loading
            }
            when (val result = getRecentSentencePatternsUseCase()) {
                is Result.Success -> _recentSentencePatterns.value = UiState.Success(result.data)
                is Result.Error -> _recentSentencePatterns.value = UiState.Error(result.message)
                else -> {}
            }
        }
    }

    private fun loadAllSentencePatterns() {
        viewModelScope.launch {
            if (_allSentencePatterns.value !is UiState.Success) {
                _allSentencePatterns.value = UiState.Loading
            }
            when (val result = getSentencePatternsUseCase()) {
                is Result.Success -> _allSentencePatterns.value = UiState.Success(result.data)
                is Result.Error -> _allSentencePatterns.value = UiState.Error(result.message)
                else -> {}
            }
        }
    }
}
