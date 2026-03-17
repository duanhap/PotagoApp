package com.example.potago.presentation.screen.myvideo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.data.local.JobDataStore
import com.example.potago.data.local.ProcessingJob
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Video
import com.example.potago.domain.usecase.GetMyVideosUseCase
import com.example.potago.presentation.screen.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyVideoViewModel @Inject constructor(
    private val getMyVideosUseCase: GetMyVideosUseCase,
    private val jobDataStore: JobDataStore
) : ViewModel() {

    private val _videos = MutableStateFlow<List<Video>>(emptyList())
    val videos: StateFlow<List<Video>> = _videos

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Loading)
    val uiState: StateFlow<UiState<Unit>> = _uiState

    private val _selectedTab = MutableStateFlow("All")
    val selectedTab: StateFlow<String> = _selectedTab

    private val _processingJob = MutableStateFlow<ProcessingJob?>(null)
    val processingJob = _processingJob.asStateFlow()

    private var currentPage = 1
    private val pageSize = 10
    private var isLastPage = false
    private var isFetching = false

    init {
        loadVideos(reset = true)
        observeProcessingJob()
    }

    private fun observeProcessingJob() {
        viewModelScope.launch {
            jobDataStore.getJob().collect { job ->
                _processingJob.value = job
            }
        }
    }

    fun onTabSelected(tab: String) {
        if (_selectedTab.value != tab) {
            _selectedTab.value = tab
            loadVideos(reset = true)
        }
    }

    fun loadMoreVideos() {
        if (!isLastPage && !isFetching) {
            loadVideos(reset = false)
        }
    }
    
    fun refresh() {
        loadVideos(reset = true)
    }

    private fun loadVideos(reset: Boolean) {
        if (isFetching && !reset) return
        isFetching = true

        if (reset) {
            currentPage = 1
            isLastPage = false
            // Không nên xóa list ngay lập tức để tránh bị nháy màn hình khi refresh
            // _videos.value = emptyList() 
            if (_videos.value.isEmpty()) _uiState.value = UiState.Loading
        }

        viewModelScope.launch {
            val typeVideo = when (_selectedTab.value) {
                "Youtube" -> "youtube"
                "File" -> "file"
                else -> null
            }

            when (val result = getMyVideosUseCase(typeVideo, currentPage, pageSize)) {
                is Result.Success -> {
                    val newVideos = result.data
                    if (reset) {
                        _videos.value = newVideos
                        currentPage = 2
                        isLastPage = newVideos.size < pageSize
                    } else {
                        if (newVideos.isEmpty()) {
                            isLastPage = true
                        } else {
                            _videos.value = _videos.value + newVideos
                            currentPage++
                            if (newVideos.size < pageSize) {
                                isLastPage = true
                            }
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
