package com.example.potago.presentation.screen.myvideo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.data.local.JobDataStore
import com.example.potago.data.local.ProcessingJob
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Video
import com.example.potago.domain.usecase.GetMyVideosUseCase
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
class MyVideoViewModel @Inject constructor(
    private val getMyVideosUseCase: GetMyVideosUseCase,
    private val jobDataStore: JobDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyVideoUiState())
    val uiState: StateFlow<MyVideoUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var currentPage = 1
    private val pageSize = 10

    init {
        loadVideos(reset = true)
        observeProcessingJob()
    }

    private fun observeProcessingJob() {
        viewModelScope.launch {
            jobDataStore.getJob().collect { job ->
                _uiState.update { it.copy(processingJob = job) }
            }
        }
    }

    fun onEvent(event: MyVideoEvent) {
        when (event) {
            is MyVideoEvent.TabSelected -> {
                if (_uiState.value.selectedTab != event.tab) {
                    _uiState.update { it.copy(selectedTab = event.tab) }
                    loadVideos(reset = true)
                }
            }
            is MyVideoEvent.VideoClicked -> {
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.Navigate(Screen.DetailedVideo(event.videoId)))
                }
            }
            MyVideoEvent.LoadMore -> {
                if (!_uiState.value.isLastPage && !_uiState.value.isFetching) {
                    loadVideos(reset = false)
                }
            }
            MyVideoEvent.Refresh -> {
                loadVideos(reset = true)
            }
            MyVideoEvent.NavigateToAddVideo -> {
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.Navigate(Screen.AddVideo.route))
                }
            }
            MyVideoEvent.NavigateToManageVideo -> {
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.Navigate(Screen.ManageVideo.route))
                }
            }
        }
    }

    private fun loadVideos(reset: Boolean) {
        if (_uiState.value.isFetching && !reset) return

        if (reset) {
            currentPage = 1
            _uiState.update { 
                it.copy(
                    isFetching = true, 
                    isLastPage = false,
                    videosUiState = if (it.videos.isEmpty()) UiState.Loading else it.videosUiState
                ) 
            }
        } else {
            _uiState.update { it.copy(isFetching = true) }
        }

        viewModelScope.launch {
            val typeVideo = when (_uiState.value.selectedTab) {
                "Youtube" -> "youtube"
                "File" -> "file"
                else -> null
            }

            when (val result = getMyVideosUseCase(typeVideo, currentPage, pageSize)) {
                is Result.Success -> {
                    val newVideos = result.data
                    _uiState.update { state ->
                        val updatedVideos = if (reset) newVideos else state.videos + newVideos
                        state.copy(
                            videos = updatedVideos,
                            videosUiState = UiState.Success(Unit),
                            isLastPage = newVideos.size < pageSize,
                            isFetching = false
                        )
                    }
                    if (reset) {
                        currentPage = 2
                    } else if (newVideos.isNotEmpty()) {
                        currentPage++
                    }
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(
                            videosUiState = UiState.Error(result.message ?: "Unknown error"),
                            isFetching = false
                        ) 
                    }
                }
                else -> {
                    _uiState.update { it.copy(isFetching = false) }
                }
            }
        }
    }
}

data class MyVideoUiState(
    val videos: List<Video> = emptyList(),
    val videosUiState: UiState<Unit> = UiState.Loading,
    val selectedTab: String = "All",
    val processingJob: ProcessingJob? = null,
    val isFetching: Boolean = false,
    val isLastPage: Boolean = false
)

sealed class MyVideoEvent {
    data class TabSelected(val tab: String) : MyVideoEvent()
    data class VideoClicked(val videoId: Int) : MyVideoEvent()
    object LoadMore : MyVideoEvent()
    object Refresh : MyVideoEvent()
    object NavigateToAddVideo : MyVideoEvent()
    object NavigateToManageVideo : MyVideoEvent()
}
