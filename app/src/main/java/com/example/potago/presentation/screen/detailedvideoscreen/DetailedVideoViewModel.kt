package com.example.potago.presentation.screen.detailedvideoscreen

import android.app.Application
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
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
import java.io.File
import javax.inject.Inject

enum class CheckResult {
    NONE, CORRECT, INCORRECT
}

enum class RecordState {
    IDLE, READY, RECORDING
}

@HiltViewModel
class DetailedVideoViewModel @Inject constructor(
    private val getSubtitlesUseCase: GetSubtitlesUseCase,
    private val getVideoUseCase: GetVideoUseCase,
    private val application: Application,
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

    // Logic Record Test Mode
    private val _isRecordTestMode = MutableStateFlow(false)
    val isRecordTestMode: StateFlow<Boolean> = _isRecordTestMode.asStateFlow()

    private val _recordState = MutableStateFlow(RecordState.IDLE)
    val recordState: StateFlow<RecordState> = _recordState.asStateFlow()

    private val _speakingScore = MutableStateFlow(0)
    val speakingScore: StateFlow<Int> = _speakingScore.asStateFlow()

    private val _correctSpeakingSubtitleIds = MutableStateFlow<Set<Int>>(emptySet())
    val speakingProgress: StateFlow<Int> = MutableStateFlow(0).apply {
        viewModelScope.launch {
            _correctSpeakingSubtitleIds.collect { ids ->
                value = ids.size.coerceAtMost(3)
            }
        }
    }.asStateFlow()

    // Danh sách các từ đã được nói đúng (index của từ trong câu)
    private val _spokenWordIndices = MutableStateFlow<Set<Int>>(emptySet())
    val spokenWordIndices: StateFlow<Set<Int>> = _spokenWordIndices.asStateFlow()

    // Speech Recognition & Audio recording
    private var speechRecognizer: SpeechRecognizer? = null
    private val audioFilePath: String by lazy {
        File(application.cacheDir, "user_recording.m4a").absolutePath
    }
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null

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
        if (index != 1) {
            exitRecordTestMode()
        }
    }

    fun updateCurrentTime(time: Long) {
        _currentTimeMs.value = time
    }

    fun nextSubtitle(total: Int) {
        if (_currentSubtitleIndex.value < total - 1) {
            _currentSubtitleIndex.value++
            resetQuestionState()
            resetRecordState()
            if (_isRepeatMode.value) {
                _isRepeatMode.value = false
            }
        }
    }

    fun prevSubtitle() {
        if (_currentSubtitleIndex.value > 0) {
            _currentSubtitleIndex.value--
            resetQuestionState()
            resetRecordState()
            if (_isRepeatMode.value) {
                _isRepeatMode.value = false
            }
        }
    }

    fun jumpToSubtitle(index: Int) {
        _currentSubtitleIndex.value = index
        resetQuestionState()
        resetRecordState()
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
            // Keep alphanumeric and common Asian/Vietnamese characters
            // Improved regex to support Vietnamese tone marks and other unicode letters
            .replace(Regex("[^a-z0-9\\s\\u3040-\\u309F\\u30A0-\\u30FF\\u4E00-\\u9FFF\\u00C0-\\u1EF9]"), "")
            .trim()
            .replace(Regex("\\s+"), " ")
    }

    // Record Test Mode Logic
    fun enterRecordTestMode() {
        _isRecordTestMode.value = true
        _recordState.value = RecordState.READY
        _isRepeatMode.value = false
        _speakingScore.value = 0
        _spokenWordIndices.value = emptySet()
    }

    fun exitRecordTestMode() {
        if (_speakingScore.value >= 80) {
            getCurrentSubtitle()?.let { sub ->
                val newSet = _correctSpeakingSubtitleIds.value.toMutableSet()
                newSet.add(sub.id)
                _correctSpeakingSubtitleIds.value = newSet
            }
        }
        _isRecordTestMode.value = false
        _recordState.value = RecordState.IDLE
        _speakingScore.value = 0
        _spokenWordIndices.value = emptySet()
        stopAudioPlayback()
    }

    fun toggleRecord() {
        if (_recordState.value == RecordState.READY) {
            startListeningAndRecording()
        } else if (_recordState.value == RecordState.RECORDING) {
            stopListeningAndRecording()
        }
    }

    private fun startListeningAndRecording() {
        _recordState.value = RecordState.RECORDING
        _speakingScore.value = 0
        _spokenWordIndices.value = emptySet()

        // 1. Start Media Recording for playback
        try {
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION) // Changed to VOICE_RECOGNITION to reduce conflict
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(audioFilePath)
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e("Record", "MediaRecorder failed: ${e.message}")
            try {
                // Fallback to standard MIC if VOICE_RECOGNITION fails
                mediaRecorder?.release()
                mediaRecorder = MediaRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    setOutputFile(audioFilePath)
                    prepare()
                    start()
                }
            } catch (e2: Exception) {
                Log.e("Record", "MediaRecorder fallback failed: ${e2.message}")
            }
        }

        // 2. Start Speech Recognition
        val lang = (videoState.value as? UiState.Success)?.data?.termLanguageCode ?: "en-US"
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(application)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                Log.e("Speech", "Error code: $error")
                // Only stop if it's not a temporary error
                if (_recordState.value == RecordState.RECORDING) {
                    stopListeningAndRecording()
                }
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val spokenText = matches?.firstOrNull() ?: ""
                Log.d("Speech", "Final results: $spokenText")
                calculateSpeakingResult(spokenText)
                if (_recordState.value == RecordState.RECORDING) {
                    stopListeningAndRecording()
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val spokenText = matches?.firstOrNull() ?: ""
                calculateSpeakingResult(spokenText)
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        
        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Log.e("Speech", "SpeechRecognizer failed: ${e.message}")
        }
    }

    private fun stopListeningAndRecording() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.e("Speech", "Stop listening failed: ${e.message}")
        }
        
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
        } catch (e: Exception) {
            Log.e("Record", "MediaRecorder stop failed: ${e.message}")
        }
        _recordState.value = RecordState.READY
    }

    fun playBackLastRecord() {
        val file = File(audioFilePath)
        if (!file.exists() || file.length() == 0L) {
            Log.e("Playback", "Recording file not found or empty")
            return
        }

        stopAudioPlayback()
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(audioFilePath)
                prepare()
                start()
                setOnCompletionListener { 
                    it.release()
                    mediaPlayer = null 
                }
            } catch (e: Exception) {
                Log.e("Playback", "MediaPlayer failed: ${e.message}")
            }
        }
    }

    private fun stopAudioPlayback() {
        mediaPlayer?.let {
            try {
                if (it.isPlaying) it.stop()
            } catch (e: Exception) {
                Log.e("Playback", "Stop failed: ${e.message}")
            }
            it.release()
        }
        mediaPlayer = null
    }

    private fun calculateSpeakingResult(spokenText: String) {
        if (spokenText.isBlank()) return

        val currentSub = getCurrentSubtitle() ?: return
        val originalText = currentSub.content ?: ""
        
        // 1. Calculate main score using Similarity % (Levenshtein distance)
        val score = similarityScore(originalText, spokenText)
        _speakingScore.value = score

        // 2. Highlight words
        val originalWords = originalText.split(Regex("\\s+")).filter { it.isNotBlank() }
        val spokenWords = spokenText.split(Regex("\\s+")).filter { it.isNotBlank() }
        
        val newIndices = mutableSetOf<Int>()
        val normalizedSpokenWords = spokenWords.map { normalizeText(it) }

        for (i in originalWords.indices) {
            val originalWord = normalizeText(originalWords[i])
            // Nếu từ gốc xuất hiện trong đoạn nói của user thì xem như đúng từ đó (highlight)
            if (normalizedSpokenWords.contains(originalWord)) {
                newIndices.add(i)
            }
        }
        _spokenWordIndices.value = newIndices
    }

    private fun similarityScore(a: String, b: String): Int {
        val s1 = normalizeText(a)
        val s2 = normalizeText(b)
        if (s1.isEmpty() && s2.isEmpty()) return 100
        if (s1.isEmpty() || s2.isEmpty()) return 0
        
        val distance = levenshtein(s1, s2)
        val maxLen = maxOf(s1.length, s2.length)
        val similarity = 1.0 - (distance.toDouble() / maxLen)
        return (similarity * 100).toInt()
    }

    private fun levenshtein(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j
        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[s1.length][s2.length]
    }

    private fun resetRecordState() {
        if (_isRecordTestMode.value) {
            _recordState.value = RecordState.READY
            _speakingScore.value = 0
            _spokenWordIndices.value = emptySet()
            stopAudioPlayback()
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer?.destroy()
        mediaRecorder?.release()
        mediaPlayer?.release()
    }
}
