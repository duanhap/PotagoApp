package com.example.potago.presentation.screen.addvideo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.data.local.JobDataStore
import com.example.potago.data.local.ProcessingJob
import com.example.potago.domain.model.Result
import com.example.potago.domain.repository.VideoRepository
import com.example.potago.presentation.screen.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddVideoUiState(
    val youtubeLink: String = "",
    val filePath: String = "",
    val termLanguage: Language = Language.JAPANESE,
    val definitionLanguage: Language = Language.VIETNAMESE,
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class Language(val displayName: String, val code: String) {
    VIETNAMESE("Tiếng Việt", "vi"),
    ENGLISH("English", "en"),
    JAPANESE("日本語", "ja"),
    CHINESE("汉语", "zh")
}

@HiltViewModel
class AddVideoViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val jobDataStore: JobDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddVideoUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onYoutubeLinkChange(link: String) {
        _uiState.update { it.copy(youtubeLink = link) }
    }

    fun onFilePathChange(path: String) {
        _uiState.update { it.copy(filePath = path) }
    }

    fun onTermLanguageChange(language: Language) {
        _uiState.update { it.copy(termLanguage = language) }
    }

    fun onDefinitionLanguageChange(language: Language) {
        _uiState.update { it.copy(definitionLanguage = language) }
    }

    fun onStartClick() {
        val state = _uiState.value
        
        // Validation
        if (state.termLanguage == state.definitionLanguage) {
            sendUiEvent(UiEvent.ShowSnackbar("Thuật ngữ và định nghĩa không được trùng ngôn ngữ"))
            return
        }

        if (state.youtubeLink.isNotBlank() && state.filePath.isNotBlank()) {
            sendUiEvent(UiEvent.ShowSnackbar("Vui lòng chỉ chọn 1 nguồn video (Youtube hoặc File)"))
            return
        }

        if (state.youtubeLink.isBlank() && state.filePath.isBlank()) {
            sendUiEvent(UiEvent.ShowSnackbar("Vui lòng nhập link Youtube hoặc chọn file video"))
            return
        }

        viewModelScope.launch {
            val currentJob = jobDataStore.getJob().firstOrNull()
            if (currentJob != null) {
                sendUiEvent(UiEvent.ShowSnackbar("Hiện tại đang có 1 video đang xử lý, vui lòng đợi"))
                return@launch
            }
            createVideo()
        }
    }

    private fun createVideo() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val state = _uiState.value
            
            val sourceUrl = if (state.youtubeLink.isNotBlank()) state.youtubeLink else state.filePath
            val typeVideo = if (state.youtubeLink.isNotBlank()) "youtube" else "file"

            val result = videoRepository.createMyVideo(
                title = null,
                thumbnail = null,
                sourceUrl = sourceUrl,
                typeVideo = typeVideo,
                definitionLangCode = state.definitionLanguage.code,
                termLangCode = state.termLanguage.code
            )

            when (result) {
                is Result.Success -> {
                    val (video, jobId) = result.data
                    if (jobId != null) {
                        jobDataStore.saveJob(ProcessingJob(video = video, jobId = jobId))
                    }
                    _uiState.update { it.copy(isLoading = false) }
                    sendUiEvent(UiEvent.Navigate("manage_video"))
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                    sendUiEvent(UiEvent.ShowSnackbar(result.message))
                }
                else -> {}
            }
        }
    }

    fun onUploadClick() {
        sendUiEvent(UiEvent.ShowSnackbar("Tính năng này đang được phát triển"))
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}
