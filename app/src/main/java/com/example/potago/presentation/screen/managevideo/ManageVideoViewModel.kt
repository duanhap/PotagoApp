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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ManageVideoUiState(
    val processedVideos: List<Video> = emptyList(),
    val processingJob: ProcessingJob? = null,
    val uiState: UiState<Unit> = UiState.Idle,
    val isLastPage: Boolean = false,
    val currentPage: Int = 1,
    val isDeleting: Boolean = false,
    val isCanceling: Boolean = false,
    val deleteResult: UiState<Unit> = UiState.Idle
)

@HiltViewModel
class ManageVideoViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val jobDataStore: JobDataStore,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManageVideoUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val pageSize = 10
    private var pollingJob: Job? = null

    init {
        loadProcessedVideos()
        observeProcessingJob()
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
                    is Result.Error -> {}
                    else -> {}
                }
                delay(3000)
            }
        }
    }

    fun loadProcessedVideos(isRefresh: Boolean = false) {
        if (_uiState.value.uiState is UiState.Loading && !isRefresh) return
        if (!isRefresh && _uiState.value.isLastPage) return

        val pageToLoad = if (isRefresh) 1 else _uiState.value.currentPage

        _uiState.update { it.copy(uiState = if (isRefresh) UiState.Idle else UiState.Loading) }

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
                    _uiState.update { it.copy(uiState = UiState.Error(result.message)) }
                }
                else -> {}
            }
        }
    }

    fun deleteVideo(videoId: Int) {
        _uiState.update { it.copy(isDeleting = true, deleteResult = UiState.Loading) }
        viewModelScope.launch {
            val result = videoRepository.deleteVideo(videoId)
            when (result) {
                is Result.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            processedVideos = state.processedVideos.filter { it.id != videoId },
                            isDeleting = false,
                            deleteResult = UiState.Success(Unit)
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isDeleting = false, deleteResult = UiState.Error(result.message)) }
                }
                else -> {}
            }
        }
    }

    fun cancelJob() {
        val currentJob = _uiState.value.processingJob ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isCanceling = true) }
            val result = videoRepository.cancelJob(currentJob.video.id, currentJob.jobId)
            when (result) {
                is Result.Success -> {
                    jobDataStore.clearJob()
                    _uiEvent.emit(UiEvent.ShowSnackbar("Đã hủy xử lý video thành công"))
                    _uiState.update { it.copy(isCanceling = false) }
                    loadProcessedVideos(isRefresh = true)
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isCanceling = false) }
                    _uiEvent.emit(UiEvent.ShowSnackbar("Lỗi: ${result.message}"))
                }
                else -> {}
            }
        }
    }

    fun resetDeleteResult() {
        _uiState.update { it.copy(deleteResult = UiState.Idle) }
    }
}
