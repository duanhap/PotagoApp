package com.example.potago.presentation.screen.recommendvideo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Video
import com.example.potago.domain.usecase.GetPublicVideosUseCase
import com.example.potago.presentation.screen.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendVideoViewModel @Inject constructor(
    private val getPublicVideosUseCase: GetPublicVideosUseCase
) : ViewModel() {

    private val _videos = MutableStateFlow<List<Video>>(emptyList())
    val videos: StateFlow<List<Video>> = _videos

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Loading)
    val uiState: StateFlow<UiState<Unit>> = _uiState

    private var currentPage = 1
    private val pageSize = 10
    private var isLastPage = false
    private var isFetching = false

    private val langMap = mapOf(0 to "en", 1 to "ja", 2 to "zh")
    private var currentLangIndex = 0

    init {
        loadVideos(reset = true)
    }

    fun onLanguageTabSelected(index: Int) {
        if (currentLangIndex != index) {
            currentLangIndex = index
            loadVideos(reset = true)
        }
    }

    fun loadMoreVideos() {
        if (!isLastPage && !isFetching) {
            loadVideos(reset = false)
        }
    }

    private fun loadVideos(reset: Boolean) {
        if (isFetching) return
        isFetching = true

        if (reset) {
            currentPage = 1
            isLastPage = false
            _videos.value = emptyList()
            _uiState.value = UiState.Loading
        }

        viewModelScope.launch {
            val langCode = langMap[currentLangIndex] ?: "en"
            when (val result = getPublicVideosUseCase(langCode, currentPage, pageSize)) {
                is Result.Success -> {
                    val newVideos = result.data
                    if (newVideos.isEmpty()) {
                        isLastPage = true
                    } else {
                        _videos.value = _videos.value + newVideos
                        currentPage++
                        if (newVideos.size < pageSize) {
                            isLastPage = true
                        }
                    }
                    _uiState.value = UiState.Success(Unit)
                }
                is Result.Error -> {
                    _uiState.value = UiState.Error(result.message ?: "Unknown error")
                }
                else -> {}
            }
            isFetching = false
        }
    }
}
