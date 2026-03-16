package com.example.potago.presentation.screen.video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Video
import com.example.potago.domain.usecase.GetMyVideosUseCase
import com.example.potago.domain.usecase.GetPublicVideosUseCase
import com.example.potago.presentation.screen.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val getPublicVideosUseCase: GetPublicVideosUseCase,
    private val getMyVideosUseCase: GetMyVideosUseCase
) : ViewModel() {

    private val _recommendedVideos = MutableStateFlow<UiState<List<Video>>>(UiState.Loading)
    val recommendedVideos: StateFlow<UiState<List<Video>>> = _recommendedVideos

    private val _myVideos = MutableStateFlow<UiState<List<Video>>>(UiState.Loading)
    val myVideos: StateFlow<UiState<List<Video>>> = _myVideos

    private val _selectedLangIndex = MutableStateFlow(0)
    val selectedLangIndex: StateFlow<Int> = _selectedLangIndex

    private val langMap = mapOf(0 to "en", 1 to "ja", 2 to "zh")

    init {
        loadRecommendedVideos(langMap[0] ?: "en")
        loadMyVideos()
    }

    fun onLanguageTabSelected(index: Int) {
        if (_selectedLangIndex.value != index) {
            _selectedLangIndex.value = index
            loadRecommendedVideos(langMap[index] ?: "en")
        }
    }

    private fun loadRecommendedVideos(langCode: String) {
        viewModelScope.launch {
            _recommendedVideos.value = UiState.Loading
            when (val result = getPublicVideosUseCase(langCode, 1, 3)) {
                is Result.Success -> {
                    _recommendedVideos.value = UiState.Success(result.data)
                }
                is Result.Error -> {
                    _recommendedVideos.value = UiState.Error(result.message ?: "Unknown error")
                }
                else -> {}
            }
        }
    }

    fun loadMyVideos() {
        viewModelScope.launch {
            _myVideos.value = UiState.Loading
            when (val result = getMyVideosUseCase(null, 1, 3)) {
                is Result.Success -> {
                    _myVideos.value = UiState.Success(result.data)
                }
                is Result.Error -> {
                    _myVideos.value = UiState.Error(result.message ?: "Unknown error")
                }
                else -> {}
            }
        }
    }
}
