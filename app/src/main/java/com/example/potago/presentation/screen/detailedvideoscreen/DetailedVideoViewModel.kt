package com.example.potago.presentation.screen.detailedvideoscreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Subtitle
import com.example.potago.domain.model.Video
import com.example.potago.domain.usecase.GetSubtitlesUseCase
import com.example.potago.domain.usecase.GetVideoUseCase
import com.example.potago.presentation.screen.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailedVideoViewModel @Inject constructor(
    private val getSubtitlesUseCase: GetSubtitlesUseCase,
    private val getVideoUseCase: GetVideoUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val videoId: Int = checkNotNull(savedStateHandle["videoId"])

    private val _subtitlesState = MutableStateFlow<UiState<List<Subtitle>>>(UiState.Loading)
    val subtitlesState: StateFlow<UiState<List<Subtitle>>> = _subtitlesState.asStateFlow()

    private val _videoState = MutableStateFlow<UiState<Video>>(UiState.Loading)
    val videoState: StateFlow<UiState<Video>> = _videoState.asStateFlow()

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex.asStateFlow()

    // Quản lý thời gian thực của video
    private val _currentTimeMs = MutableStateFlow(0L)
    val currentTimeMs: StateFlow<Long> = _currentTimeMs.asStateFlow()

    // Quản lý câu hiện tại trong tab "Xem từng câu"
    private val _currentSubtitleIndex = MutableStateFlow(0)
    val currentSubtitleIndex: StateFlow<Int> = _currentSubtitleIndex.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            loadVideo()
        }
    }

    private suspend fun loadVideo() {
        _videoState.value = UiState.Loading
        when (val result = getVideoUseCase(videoId)) {
            is Result.Success -> {
                val video = result.data
                _videoState.value = UiState.Success(video)
                // Sau khi có video, kiểm tra publicVideoId để lấy sub
                val idForSubtitles = video.publicVideoId ?: video.id
                loadSubtitles(idForSubtitles)
            }
            is Result.Error -> {
                _videoState.value = UiState.Error(result.message)
            }
            is Result.Loading -> {
                _videoState.value = UiState.Loading
            }
        }
    }

    private suspend fun loadSubtitles(id: Int) {
        _subtitlesState.value = UiState.Loading
        when (val result = getSubtitlesUseCase(id)) {
            is Result.Success -> {
                _subtitlesState.value = UiState.Success(result.data)
            }
            is Result.Error -> {
                _subtitlesState.value = UiState.Error(result.message)
            }
            is Result.Loading -> {
                _subtitlesState.value = UiState.Loading
            }
        }
    }

    fun onTabSelected(index: Int) {
        _selectedTabIndex.value = index
    }

    fun updateCurrentTime(time: Long) {
        _currentTimeMs.value = time
    }

    fun nextSubtitle(total: Int) {
        if (_currentSubtitleIndex.value < total - 1) {
            _currentSubtitleIndex.value++
        }
    }

    fun prevSubtitle() {
        if (_currentSubtitleIndex.value > 0) {
            _currentSubtitleIndex.value--
        }
    }

    fun jumpToSubtitle(index: Int) {
        _currentSubtitleIndex.value = index
    }
}
