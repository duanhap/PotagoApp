package com.example.potago.presentation.screen.managevideo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.data.local.JobDataStore
import com.example.potago.data.local.ProcessingJob
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Video
import com.example.potago.domain.repository.VideoRepository
import com.example.potago.presentation.screen.UiEvent
import com.example.potago.presentation.screen.UiState
import com.example.potago.util.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageVideoViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val jobDataStore: JobDataStore,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManageVideoUiState())
    val uiState: StateFlow<ManageVideoUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val pageSize = 10
    private var pollingJob: Job? = null

    init {
        loadProcessedVideos(isRefresh = true)
        observeProcessingJob()
    }

    fun onEvent(event: ManageVideoEvent) {
        when (event) {
            ManageVideoEvent.LoadMore -> {
                if (!_uiState.value.isLastPage && _uiState.value.uiState !is UiState.Loading) {
                    loadProcessedVideos(isRefresh = false)
                }
            }
            is ManageVideoEvent.DeleteVideo -> {
                deleteVideo(event.videoId)
            }
            ManageVideoEvent.CancelJob -> {
                cancelJob()
            }
            ManageVideoEvent.Refresh -> {
                loadProcessedVideos(isRefresh = true)
            }
        }
    }

    private fun observeProcessingJob() {
        viewModelScope.launch {
            jobDataStore.getJob().collect { job ->
                _uiState.update { it.copy(processingJob = job) }
                if (job != null && pollingJob == null) {
                    startPolling(job)
                } else if (job == null) {
                    pollingJob?.cancel()
                    pollingJob = null
                }
            }
        }
    }

    private fun startPolling(job: ProcessingJob) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                val result = videoRepository.syncJobStatus(job.video.id, job.jobId)
                when (result) {
                    is Result.Success -> {
                        val status = result.data
                        if (status.status == "completed" || status.status == "success") {
                            notificationHelper.showVideoCompletedNotification(job.video.title)
                            jobDataStore.clearJob()
                            loadProcessedVideos(isRefresh = true)
                            break
                        } else if (status.status == "failed") {
                            jobDataStore.clearJob()
                            break
                        } else {
                            jobDataStore.saveJob(job.copy(
                                progress = status.progress ?: job.progress,
                                status = status.status
                            ))
                        }
                    }
                    else -> {}
                }
                delay(3000)
            }
        }
    }

    private fun loadProcessedVideos(isRefresh: Boolean) {
        val pageToLoad = if (isRefresh) 1 else _uiState.value.currentPage

        if (isRefresh) {
            _uiState.update { it.copy(uiState = UiState.Loading, isLastPage = false) }
        } else {
            _uiState.update { it.copy(uiState = UiState.Loading) }
        }

        viewModelScope.launch {
            val result = videoRepository.getMyVideos(typeVideo = null, page = pageToLoad, size = pageSize)
            when (result) {
                is Result.Success -> {
                    val newVideos = result.data
                    _uiState.update { state ->
                        state.copy(
                            processedVideos = if (isRefresh) newVideos else state.processedVideos + newVideos,
                            uiState = UiState.Success(Unit),
                            currentPage = pageToLoad + 1,
                            isLastPage = newVideos.size < pageSize
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(uiState = UiState.Error(result.message ?: "Unknown error")) }
                }
                else -> {}
            }
        }
    }

    private fun deleteVideo(videoId: Int) {
        _uiState.update { it.copy(isDeleting = true) }
        viewModelScope.launch {
            val result = videoRepository.deleteVideo(videoId)
            when (result) {
                is Result.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            processedVideos = state.processedVideos.filter { it.id != videoId },
                            isDeleting = false
                        )
                    }
                    _uiEvent.send(UiEvent.ShowSnackbar("Xóa video thành công"))
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isDeleting = false) }
                    _uiEvent.send(UiEvent.ShowSnackbar(result.message ?: "Lỗi khi xóa video"))
                }
                else -> {
                    _uiState.update { it.copy(isDeleting = false) }
                }
            }
        }
    }

    private fun cancelJob() {
        val currentJob = _uiState.value.processingJob ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isCanceling = true) }
            val result = videoRepository.cancelJob(currentJob.video.id, currentJob.jobId)
            when (result) {
                is Result.Success -> {
                    jobDataStore.clearJob()
                    _uiEvent.send(UiEvent.ShowSnackbar("Đã hủy xử lý video thành công"))
                    _uiState.update { it.copy(isCanceling = false) }
                    loadProcessedVideos(isRefresh = true)
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isCanceling = false) }
                    _uiEvent.send(UiEvent.ShowSnackbar("Lỗi: ${result.message}"))
                }
                else -> {
                    _uiState.update { it.copy(isCanceling = false) }
                }
            }
        }
    }
}

data class ManageVideoUiState(
    val processedVideos: List<Video> = emptyList(),
    val processingJob: ProcessingJob? = null,
    val uiState: UiState<Unit> = UiState.Idle,
    val isLastPage: Boolean = false,
    val currentPage: Int = 1,
    val isDeleting: Boolean = false,
    val isCanceling: Boolean = false
)

sealed class ManageVideoEvent {
    object LoadMore : ManageVideoEvent()
    data class DeleteVideo(val videoId: Int) : ManageVideoEvent()
    object CancelJob : ManageVideoEvent()
    object Refresh : ManageVideoEvent()
}
