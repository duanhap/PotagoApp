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

enum class CheckResult {
    NONE, CORRECT, INCORRECT
}

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

    // Chế độ lặp lại câu (Repeat Mode)
    private val _isRepeatMode = MutableStateFlow(false)
    val isRepeatMode: StateFlow<Boolean> = _isRepeatMode.asStateFlow()
    private val _isQuestionMode = MutableStateFlow(false)
    val isQuestionMode: StateFlow<Boolean> = _isQuestionMode.asStateFlow()

    // Logic Question Mode
    private val _userInput = MutableStateFlow("")
    val userInput: StateFlow<String> = _userInput.asStateFlow()

    private val _checkResult = MutableStateFlow(CheckResult.NONE)
    val checkResult: StateFlow<CheckResult> = _checkResult.asStateFlow()

    private val _correctSubtitleIds = MutableStateFlow<Set<Int>>(emptySet())
    val writingProgress: StateFlow<Int> = MutableStateFlow(0).apply { 
        viewModelScope.launch {
            _correctSubtitleIds.collect { ids ->
                value = ids.size.coerceAtMost(3)
            }
        }
    }.asStateFlow()

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
            resetQuestionState()
            if (_isRepeatMode.value) {
                _isRepeatMode.value = false
            }
        }
    }

    fun prevSubtitle() {
        if (_currentSubtitleIndex.value > 0) {
            _currentSubtitleIndex.value--
            resetQuestionState()
            if (_isRepeatMode.value) {
                _isRepeatMode.value = false
            }
        }
    }

    fun jumpToSubtitle(index: Int) {
        _currentSubtitleIndex.value = index
        resetQuestionState()
    }

    fun toggleRepeatMode() {
        _isRepeatMode.value = !_isRepeatMode.value
    }

    fun toggleQuestionMode() {
        _isQuestionMode.value = !_isQuestionMode.value
        resetQuestionState()
    }

    fun disableRepeatMode() {
        _isRepeatMode.value = false
    }

    fun onUserInputChange(text: String) {
        _userInput.value = text
    }

    fun checkAnswer() {
        val currentSub = getCurrentSubtitle() ?: return
        val expected = normalizeText(currentSub.content ?: "")
        val actual = normalizeText(_userInput.value)

        if (expected == actual) {
            _checkResult.value = CheckResult.CORRECT
            val newSet = _correctSubtitleIds.value.toMutableSet()
            newSet.add(currentSub.id)
            _correctSubtitleIds.value = newSet
        } else {
            _checkResult.value = CheckResult.INCORRECT
        }
    }

    fun dismissResult() {
        if (_checkResult.value == CheckResult.CORRECT) {
            _userInput.value = ""
        }
        _checkResult.value = CheckResult.NONE
    }

    private fun resetQuestionState() {
        _userInput.value = ""
        _checkResult.value = CheckResult.NONE
    }

    private fun getCurrentSubtitle(): Subtitle? {
        val state = _subtitlesState.value
        if (state is UiState.Success) {
            return state.data?.getOrNull(_currentSubtitleIndex.value)
        }
        return null
    }

    private fun normalizeText(text: String): String {
        return text.lowercase()
            .replace(Regex("[^a-z0-9\\s\\u3040-\\u309F\\u30A0-\\u30FF\\u4E00-\\u9FFF]"), "")
            .trim()
            .replace(Regex("\\s+"), " ")
    }
}
