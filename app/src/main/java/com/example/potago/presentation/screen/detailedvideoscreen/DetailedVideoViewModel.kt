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

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            loadVideo()
            loadSubtitles()
        }
    }

    private suspend fun loadVideo() {
        _videoState.value = UiState.Loading
        when (val result = getVideoUseCase(videoId)) {
            is Result.Success -> {
                _videoState.value = UiState.Success(result.data)
            }
            is Result.Error -> {
                _videoState.value = UiState.Error(result.message)
            }
            is Result.Loading -> {
                _videoState.value = UiState.Loading
            }
        }
    }

    private suspend fun loadSubtitles() {
        _subtitlesState.value = UiState.Loading
        when (val result = getSubtitlesUseCase(videoId)) {
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
}
