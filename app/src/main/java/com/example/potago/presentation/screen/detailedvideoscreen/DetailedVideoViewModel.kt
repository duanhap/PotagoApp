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
import com.example.potago.domain.usecase.ClaimRewardUseCase
import com.example.potago.domain.usecase.GetSubtitlesUseCase
import com.example.potago.domain.usecase.GetVideoUseCase
import com.example.potago.domain.usecase.SyncUserSessionUseCase
import com.example.potago.presentation.navigation.Screen
import com.example.potago.presentation.screen.UiState
import com.example.potago.presentation.screen.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
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
    private val claimRewardUseCase: ClaimRewardUseCase,
    private val syncUserSessionUseCase: SyncUserSessionUseCase,
    private val application: Application,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

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

    // Transcript của những gì người dùng đang nói
    private val _spokenTranscript = MutableStateFlow("")
    val spokenTranscript: StateFlow<String> = _spokenTranscript.asStateFlow()

    // Speech Recognition & Audio recording
    private var speechRecognizer: SpeechRecognizer? = null
    private val audioFilePath: String by lazy {
        File(application.cacheDir, "user_recording.m4a").absolutePath
    }
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var recordedAudioBytes = java.io.ByteArrayOutputStream()
    
    // Manual timeout logic
    private var recognitionStartTime = 0L
    private var isRestartingRecognition = false

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

    private val _showRewardPopup = MutableStateFlow(false)
    val showRewardPopup: StateFlow<Boolean> = _showRewardPopup.asStateFlow()

    private val _isRewardEarned = MutableStateFlow(false)
    val isRewardEarned: StateFlow<Boolean> = _isRewardEarned.asStateFlow()

    private val _isClaimingReward = MutableStateFlow(false)

    // Lưu vị trí video để restore sau khi quay lại từ StreakScreen
    var savedVideoPositionMs: Long = 0L
    init {
        val videoId = savedStateHandle.get<Int>("videoId") ?: -1
        if (videoId != -1) {
            loadAllData(videoId)
        }
    }

    private fun loadAllData(videoId: Int) {
        viewModelScope.launch {
            // Tải song song Video và Subtitle để tiết kiệm thời gian
            launch { fetchVideo(videoId) }
            launch { fetchSubtitles(videoId) }
        }
    }

    private suspend fun fetchVideo(videoId: Int) {
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

    private suspend fun fetchSubtitles(id: Int) {
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

    fun claimReward() {
        if (_isClaimingReward.value) return
        _showRewardPopup.value = true
    }

    private suspend fun refreshDataStore() {
        syncUserSessionUseCase()
    }

    fun onRewardDismissed() {
        viewModelScope.launch {
            _isClaimingReward.value = true
            try {
                when (val result = claimRewardUseCase(
                    action = "learning-video",
                    hackExperience = false,
                    superExperience = false
                )) {
                    is Result.Success -> {
                        val streak = result.data.streak
                        if (streak.status == "created" || streak.status == "extended") {
                            _showRewardPopup.value = false
                            savedVideoPositionMs = _currentTimeMs.value
                            _currentSubtitleIndex.value = 0
                            refreshDataStore()
                            _isRewardEarned.value = true
                            _uiEvent.send(UiEvent.Navigate(Screen.Streak(streak.currentLength)))
                        } else {
                            // not_reached hoặc already_counted
                            _showRewardPopup.value = false
                            refreshDataStore()
                            _isRewardEarned.value = true
                        }
                    }
                    is Result.Error -> {
                        _uiEvent.send(UiEvent.ShowSnackbar(result.message))
                    }
                    else -> {}
                }
            } catch ( e: Exception) {
                _uiEvent.send(UiEvent.ShowSnackbar(e.message ?: "Unknown error"))
            }
        }
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
        _spokenTranscript.value = ""
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
        _spokenTranscript.value = ""
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
        if (!SpeechRecognizer.isRecognitionAvailable(application)) {
            Log.e("Speech", "Speech recognition NOT available on this device/emulator")
            // Optional: Show warning to user
        }

        _recordState.value = RecordState.RECORDING
        _speakingScore.value = 0
        _spokenWordIndices.value = emptySet()
        _spokenTranscript.value = ""

        // 1. Chuẩn bị buffer để hứng âm thanh trực tiếp từ Speech
        recordedAudioBytes.reset()
        mediaRecorder = null // Không dùng MediaRecorder nữa

        // 2. Start Speech Recognition

        // 2. Start Speech Recognition
        recognitionStartTime = System.currentTimeMillis()
        isRestartingRecognition = false
        
        val rawLang = (videoState.value as? UiState.Success)?.data?.termLanguageCode ?: "en-US"
        val lang = when(rawLang.lowercase()) {
            "en" -> "en-US"
            "vi" -> "vi-VN"
            else -> rawLang
        }
        
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(application)
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
            putExtra("android.speech.extra.DICTATION_MODE", true)
            // Ép hệ thống không ngắt quãng sớm - Dùng cả 2 kiểu key
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 7000L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 7000L)
            putExtra("android.speech.extras.SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS", 7000L)
            putExtra("android.speech.extras.SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS", 7000L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 3000L)
        }
        Log.d("Speech", "Starting recognition with timeout: 7000ms")

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("Speech", "onReadyForSpeech")
                _spokenTranscript.value = "... (Đang nghe)"
            }
            override fun onBeginningOfSpeech() {
                Log.d("Speech", "onBeginningOfSpeech")
                _spokenTranscript.value = "... (Đang nhận diện)"
            }
            override fun onRmsChanged(rmsdB: Float) {
                if (rmsdB > 2) Log.v("Speech", "Level: $rmsdB")
            }
            override fun onBufferReceived(buffer: ByteArray?) {
                // Hứng các mẩu âm thanh đang được nhận diện
                buffer?.let {
                    Log.v("Speech", "Buffer received: ${it.size} bytes")
                    recordedAudioBytes.write(it)
                }
            }
            override fun onEndOfSpeech() { Log.d("Speech", "onEndOfSpeech") }
            override fun onError(error: Int) {
                if (_recordState.value != RecordState.RECORDING) return

                Log.e("Speech", "Error code: $error")

                val timeElapsed = System.currentTimeMillis() - recognitionStartTime
                if ((error == 7 || error == 6) && timeElapsed < 5000 && !isRestartingRecognition) {
                    // Nếu lỗi do im lặng sớm (trước 5s), tự động bắt đầu nghe lại
                    Log.d("Speech", "Early timeout detected, restarting... (Time: $timeElapsed ms)")
                    isRestartingRecognition = true
                    viewModelScope.launch {
                        delay(200) // Nghỉ một chút trước khi restart
                        if (_recordState.value == RecordState.RECORDING) {
                            try {
                                speechRecognizer?.startListening(intent)
                                isRestartingRecognition = false
                            } catch (e: Exception) {
                                Log.e("Speech", "Restart failed: ${e.message}")
                            }
                        }
                    }
                } else if (_recordState.value == RecordState.RECORDING) {
                    // Nếu thực sự hết thời gian hoặc lỗi khác
                    if (error == 7 || error == 6) {
                        _spokenTranscript.value = "(Không nghe rõ, hãy thử lại)"
                    }
                    try {
                        speechRecognizer?.cancel()
                    } catch (e: Exception) {}

                    stopMediaRecorderOnly()
                    _recordState.value = RecordState.READY
                }
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val spokenText = matches?.firstOrNull() ?: ""
                Log.d("Speech", "Final: $spokenText")
                if (spokenText.isNotBlank()) {
                    _spokenTranscript.value = spokenText
                    calculateSpeakingResult(spokenText)
                }
                stopListeningAndRecording()
            }
            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val spokenText = matches?.firstOrNull() ?: ""
                Log.d("Speech", "Partial: $spokenText")
                if (spokenText.isNotBlank()) {
                    _spokenTranscript.value = spokenText
                    calculateSpeakingResult(spokenText)
                }
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer?.startListening(intent)
    }

    private fun stopListeningAndRecording() {
        if (_recordState.value != RecordState.RECORDING) return

        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.e("Speech", "Stop listening failed: ${e.message}")
        }

        stopMediaRecorderOnly()
        _recordState.value = RecordState.READY
    }

    private fun stopMediaRecorderOnly() {
        Log.d("Record", "Stopping. Total buffer size: ${recordedAudioBytes.size()}")
        // Lưu dữ liệu hứng được vào file WAV
        if (recordedAudioBytes.size() > 0) {
            saveRecordedBytesToWavFile()
        }
    }

    private fun saveRecordedBytesToWavFile() {
        val pcmData = recordedAudioBytes.toByteArray()
        val wavFile = File(audioFilePath.replace(".m4a", ".wav")) // Đổi đuôi thành .wav

        try {
            val out = java.io.FileOutputStream(wavFile)
            // Viết Header cho file WAV (16kHz, 16bit, Mono)
            writeWavHeader(out, pcmData.size)
            out.write(pcmData)
            out.close()
            Log.d("Record", "WAV file saved: ${wavFile.absolutePath} size: ${pcmData.size}")
        } catch (e: Exception) {
            Log.e("Record", "Save WAV failed: ${e.message}")
        }
    }

    private fun writeWavHeader(out: java.io.OutputStream, dataSize: Int) {
        val totalSize = dataSize + 36
        val sampleRate = 16000 // Tần số mẫu chuẩn của Speech Recognition
        val channels = 1
        val byteRate = sampleRate * 2

        out.write("RIFF".toByteArray())
        out.write(intToByteArray(totalSize))
        out.write("WAVE".toByteArray())
        out.write("fmt ".toByteArray())
        out.write(intToByteArray(16)) // Format size
        out.write(shortToByteArray(1.toShort())) // PCM
        out.write(shortToByteArray(channels.toShort()))
        out.write(intToByteArray(sampleRate))
        out.write(intToByteArray(byteRate))
        out.write(shortToByteArray(2.toShort())) // Block align
        out.write(shortToByteArray(16.toShort())) // Bits per sample
        out.write("data".toByteArray())
        out.write(intToByteArray(dataSize))
    }

    private fun intToByteArray(i: Int): ByteArray = byteArrayOf(
        (i and 0xff).toByte(),
        (i shr 8 and 0xff).toByte(),
        (i shr 16 and 0xff).toByte(),
        (i shr 24 and 0xff).toByte()
    )

    private fun shortToByteArray(s: Short): ByteArray = byteArrayOf(
        (s.toInt() and 0xff).toByte(),
        (s.toInt() shr 8 and 0xff).toByte()
    )

    fun playBackLastRecord() {
        val wavPath = audioFilePath.replace(".m4a", ".wav")
        val file = File(wavPath)
        if (!file.exists() || file.length() == 0L) {
            Log.e("Playback", "Recording file not found: $wavPath")
            return
        }

        stopAudioPlayback()
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(wavPath)
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
