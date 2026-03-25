package com.example.potago.presentation.screen.video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Video
import com.example.potago.domain.usecase.GetMyVideosUseCase
import com.example.potago.domain.usecase.GetPublicVideosUseCase
import com.example.potago.domain.usecase.GetRecentVideosUseCase
import com.example.potago.domain.usecase.OpenPublicVideoUseCase
import com.example.potago.presentation.navigation.Screen
import com.example.potago.presentation.screen.UiEvent
import com.example.potago.presentation.screen.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val getPublicVideosUseCase: GetPublicVideosUseCase,
    private val getMyVideosUseCase: GetMyVideosUseCase,
    private val getRecentVideosUseCase: GetRecentVideosUseCase,
    private val openPublicVideoUseCase: OpenPublicVideoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(VideoUiState())
    val uiState: StateFlow<VideoUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val langMap = mapOf(0 to "en", 1 to "ja", 2 to "zh")

    init {
        loadData()
    }

    fun onEvent(event: VideoEvent) {
        when (event) {
            is VideoEvent.LanguageTabSelected -> {
                if (_uiState.value.selectedLangIndex != event.index) {
                    _uiState.update { it.copy(selectedLangIndex = event.index) }
                    loadRecommendedVideos(langMap[event.index] ?: "en")
                }
            }
            is VideoEvent.RecommendedVideoClicked -> {
                onRecommendedVideoClick(event.videoId)
            }
            is VideoEvent.VideoClicked -> {
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.Navigate(Screen.DetailedVideo(event.videoId)))
                }
            }
            VideoEvent.Refresh -> {
                loadData()
            }
        }
    }

    private fun loadData() {
        loadRecommendedVideos(langMap[_uiState.value.selectedLangIndex] ?: "en")
        loadMyVideos()
        loadRecentVideos()
    }

    private fun loadRecommendedVideos(langCode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(recommendedVideos = UiState.Loading) }
            when (val result = getPublicVideosUseCase(langCode, 1, 3)) {
                is Result.Success -> {
                    _uiState.update { it.copy(recommendedVideos = UiState.Success(result.data)) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(recommendedVideos = UiState.Error(result.message ?: "Unknown error")) }
                }
                else -> {}
            }
        }
    }

    private fun onRecommendedVideoClick(publicVideoId: Int) {
        viewModelScope.launch {
            when (val result = openPublicVideoUseCase(publicVideoId)) {
                is Result.Success -> {
                    _uiEvent.send(UiEvent.Navigate(Screen.DetailedVideo(result.data.id)))
                }
                is Result.Error -> {
                    _uiEvent.send(UiEvent.ShowSnackbar(result.message ?: "Không thể mở video"))
                }
                else -> {}
            }
        }
    }

    private fun loadMyVideos() {
        viewModelScope.launch {
            _uiState.update { it.copy(myVideos = UiState.Loading) }
            when (val result = getMyVideosUseCase(null, 1, 3)) {
                is Result.Success -> {
                    _uiState.update { it.copy(myVideos = UiState.Success(result.data)) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(myVideos = UiState.Error(result.message ?: "Unknown error")) }
                }
                else -> {}
            }
        }
    }

    private fun loadRecentVideos() {
        viewModelScope.launch {
            _uiState.update { it.copy(recentVideos = UiState.Loading) }
            when (val result = getRecentVideosUseCase(1, 3)) {
                is Result.Success -> {
                    _uiState.update { it.copy(recentVideos = UiState.Success(result.data)) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(recentVideos = UiState.Error(result.message ?: "Unknown error")) }
                }
                else -> {}
            }
        }
    }
}

data class VideoUiState(
    val recommendedVideos: UiState<List<Video>> = UiState.Loading,
    val myVideos: UiState<List<Video>> = UiState.Loading,
    val recentVideos: UiState<List<Video>> = UiState.Loading,
    val selectedLangIndex: Int = 0
)

sealed class VideoEvent {
    data class LanguageTabSelected(val index: Int) : VideoEvent()
    data class RecommendedVideoClicked(val videoId: Int) : VideoEvent()
    data class VideoClicked(val videoId: Int) : VideoEvent()
    object Refresh : VideoEvent()
}
