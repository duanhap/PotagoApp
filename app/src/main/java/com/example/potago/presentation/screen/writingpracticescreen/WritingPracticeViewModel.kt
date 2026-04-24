package com.example.potago.presentation.screen.writingpracticescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Setence
import com.example.potago.domain.usecase.GetSentencesByPatternUseCase
import com.example.potago.domain.usecase.UpdateSentenceUseCase
import com.example.potago.domain.usecase.ClaimRewardUseCase
import com.example.potago.domain.usecase.CheckAndExpireItemSessionUseCase
import com.example.potago.domain.usecase.ObserveActiveItemSessionUseCase
import com.example.potago.domain.usecase.SyncUserSessionUseCase
import com.example.potago.data.local.WritingPracticeDataStore
import com.example.potago.data.local.WritingPracticeProgress
import com.example.potago.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class WritingNavEvent {
    data class ToStreak(val streakCount: Int, val xpEarned: Int, val diamondEarned: Int, val timeFormatted: String, val hackXp: Boolean = false, val superXp: Boolean = false) : WritingNavEvent()
    data class ToResult(val xpEarned: Int, val diamondEarned: Int, val timeFormatted: String, val hackXp: Boolean = false, val superXp: Boolean = false) : WritingNavEvent()
}

sealed class AnswerResult {
    object None : AnswerResult()
    object Correct : AnswerResult()
    data class Incorrect(val correctAnswer: String) : AnswerResult()
}

data class WritingPracticeUiState(
    val isLoading: Boolean = false,
    val sentences: List<Setence> = emptyList(),
    val currentIndex: Int = 0,
    val currentSentence: Setence? = null,
    val answerResult: AnswerResult = AnswerResult.None,
    val error: String? = null,
    val isCompleted: Boolean = false,
    val startTime: Long = 0L,
    val completionTime: Long = 0L,
    val experienceEarned: Int = 15,
    val diamondEarned: Int = 10,
    val incorrectSentences: MutableList<Setence> = mutableListOf(),
    val isRetryRound: Boolean = false,
    val completedSentenceIds: MutableList<Int> = mutableListOf(),
    val correctCount: Int = 0, // số câu đã trả lời đúng (không tăng khi sai)
    val totalOriginal: Int = 0, // tổng câu gốc (không đổi)
    val showContinueDialog: Boolean = false,
    val savedProgress: WritingPracticeProgress? = null,
    val hackExperience: Boolean = false,
    val superExperience: Boolean = false,
    val isSubmitting: Boolean = false
) {
    val progress: Float get() = if (totalOriginal > 0) correctCount.toFloat() / totalOriginal else 0f
}

@HiltViewModel
class WritingPracticeViewModel @Inject constructor(
    private val getSentencesByPatternUseCase: GetSentencesByPatternUseCase,
    private val updateSentenceUseCase: UpdateSentenceUseCase,
    private val claimRewardUseCase: ClaimRewardUseCase,
    private val syncUserSessionUseCase: SyncUserSessionUseCase,
    private val observeActiveItemSessionUseCase: ObserveActiveItemSessionUseCase,
    private val checkAndExpireItemSessionUseCase: CheckAndExpireItemSessionUseCase,
    private val dataStore: WritingPracticeDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(WritingPracticeUiState())
    val uiState: StateFlow<WritingPracticeUiState> = _uiState.asStateFlow()

    private val _navEvent = Channel<WritingNavEvent>()
    val navEvent = _navEvent.receiveAsFlow()

    init {
        observeActiveItems()
    }

    private fun observeActiveItems() {
        viewModelScope.launch { checkAndExpireItemSessionUseCase() }
        observeActiveItemSessionUseCase()
            .onEach { session ->
                _uiState.update {
                    it.copy(
                        hackExperience = session?.itemType == "hack_xp" && session.isActive,
                        superExperience = session?.itemType == "super_xp" && session.isActive
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun loadSentences(patternId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // Kiểm tra có progress đã lưu không
            val savedProgress = dataStore.getProgress(patternId).firstOrNull()
            
            if (savedProgress != null && savedProgress.currentIndex > 0) {
                // Có progress đã lưu -> hiện dialog xác nhận
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        showContinueDialog = true,
                        savedProgress = savedProgress
                    )
                }
            } else {
                // Không có progress -> load mới
                loadNewSession(patternId)
            }
        }
    }
    
    fun continueFromSaved() {
        viewModelScope.launch {
            val savedProgress = _uiState.value.savedProgress ?: return@launch
            val patternId = savedProgress.patternId
            
            _uiState.update { it.copy(isLoading = true, showContinueDialog = false) }
            
            when (val result = getSentencesByPatternUseCase(patternId)) {
                is Result.Success -> {
                    val allSentences = result.data.filter { it.status == "unknown" }
                    
                    // Lọc ra các câu chưa làm đúng
                    val remainingSentences = if (savedProgress.isRetryRound) {
                        // Đang retry -> chỉ lấy câu sai
                        allSentences.filter { it.id in savedProgress.incorrectSentenceIds }
                    } else {
                        // Vòng đầu -> lấy tất cả câu chưa làm
                        allSentences
                    }
                    
                    val incorrectSentences = allSentences.filter { it.id in savedProgress.incorrectSentenceIds }.toMutableList()
                    val completedIds = savedProgress.completedSentenceIds.toMutableList()
                    
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            sentences = remainingSentences,
                            currentIndex = savedProgress.currentIndex,
                            currentSentence = remainingSentences.getOrNull(savedProgress.currentIndex),
                            incorrectSentences = incorrectSentences,
                            completedSentenceIds = completedIds,
                            isRetryRound = savedProgress.isRetryRound,
                            startTime = savedProgress.startTime
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message ?: "Lỗi tải dữ liệu"
                        )
                    }
                }
                else -> {}
            }
        }
    }
    
    fun startNewSession(patternId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(showContinueDialog = false) }
            // Xóa progress cũ
            dataStore.clearProgress(patternId)
            // Load mới
            loadNewSession(patternId)
        }
    }
    
    private suspend fun loadNewSession(patternId: Int) {
        _uiState.update { it.copy(isLoading = true, error = null, startTime = System.currentTimeMillis()) }
        when (val result = getSentencesByPatternUseCase(patternId)) {
            is Result.Success -> {
                // Lấy tất cả câu rồi random 5 câu
                val allSentences = result.data.shuffled().take(5)
                
                if (allSentences.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Chưa có câu nào trong mẫu câu này"
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            sentences = allSentences,
                            currentSentence = allSentences.firstOrNull(),
                            currentIndex = 0,
                            totalOriginal = allSentences.size,
                            correctCount = 0
                        )
                    }
                }
            }
            is Result.Error -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.message ?: "Lỗi tải dữ liệu"
                    )
                }
            }
            else -> {}
        }
    }

    fun checkAnswer(userAnswer: String) {
        val currentSentence = _uiState.value.currentSentence ?: return
        val correctAnswer = currentSentence.term
        
        // Normalize và so sánh
        val isCorrect = normalizeAnswer(userAnswer) == normalizeAnswer(correctAnswer)
        
        if (isCorrect) {
            // Thêm vào danh sách đã làm đúng
            val completedIds = _uiState.value.completedSentenceIds
            val isNewCorrect = !completedIds.contains(currentSentence.id)
            if (isNewCorrect) {
                completedIds.add(currentSentence.id)
            }
            _uiState.update {
                it.copy(
                    answerResult = AnswerResult.Correct,
                    correctCount = if (isNewCorrect) it.correctCount + 1 else it.correctCount
                )
            }
            // Cập nhật status thành "known"
            updateSentenceStatus(currentSentence.id, "known")
        } else {
            _uiState.update { 
                it.copy(answerResult = AnswerResult.Incorrect(correctAnswer)) 
            }
            // Thêm câu sai vào danh sách (nếu chưa có)
            val incorrectList = _uiState.value.incorrectSentences
            if (!incorrectList.any { it.id == currentSentence.id }) {
                incorrectList.add(currentSentence)
            }
            // Tăng số lần sai
            incrementMistakes(currentSentence)
        }
        
        // Lưu progress sau mỗi câu trả lời
        saveProgress()
    }

    fun moveToNextSentence() {
        val currentIndex = _uiState.value.currentIndex
        val sentences = _uiState.value.sentences
        val incorrectSentences = _uiState.value.incorrectSentences
        val currentSentence = _uiState.value.currentSentence
        
        // Nếu câu hiện tại đúng và đang ở vòng retry, xóa khỏi danh sách câu sai
        if (_uiState.value.answerResult is AnswerResult.Correct && _uiState.value.isRetryRound) {
            incorrectSentences.removeAll { it.id == currentSentence?.id }
        }
        
        if (currentIndex + 1 < sentences.size) {
            // Còn câu tiếp theo trong danh sách hiện tại
            _uiState.update {
                it.copy(
                    currentIndex = currentIndex + 1,
                    currentSentence = sentences[currentIndex + 1],
                    answerResult = AnswerResult.None
                )
            }
            // Lưu progress
            saveProgress()
        } else {
            // Hết câu trong danh sách hiện tại
            if (incorrectSentences.isNotEmpty()) {
                // Có câu sai -> Bắt đầu vòng làm lại
                _uiState.update {
                    it.copy(
                        sentences = incorrectSentences.toList(),
                        currentIndex = 0,
                        currentSentence = incorrectSentences.firstOrNull(),
                        answerResult = AnswerResult.None,
                        isRetryRound = true
                    )
                }
                // Lưu progress
                saveProgress()
            } else {
                // Không có câu sai -> Hoàn thành
                val completionTime = System.currentTimeMillis() - _uiState.value.startTime
                _uiState.update {
                    it.copy(
                        isCompleted = true,
                        completionTime = completionTime,
                        answerResult = AnswerResult.None
                    )
                }
                // Xóa progress khi hoàn thành
                clearProgress()
                // Claim rewards
                claimRewards()
            }
        }
    }
    
    private fun saveProgress() {
        viewModelScope.launch {
            val state = _uiState.value
            val patternId = state.sentences.firstOrNull()?.setencePatternId ?: return@launch
            
            val progress = WritingPracticeProgress(
                patternId = patternId,
                currentIndex = state.currentIndex,
                completedSentenceIds = state.completedSentenceIds.toList(),
                incorrectSentenceIds = state.incorrectSentences.map { it.id },
                isRetryRound = state.isRetryRound,
                startTime = state.startTime
            )
            
            dataStore.saveProgress(progress)
        }
    }
    
    private fun clearProgress() {
        viewModelScope.launch {
            val patternId = _uiState.value.sentences.firstOrNull()?.setencePatternId ?: return@launch
            dataStore.clearProgress(patternId)
        }
    }

    private fun claimRewards() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            val timeFormatted = getCompletionTimeFormatted()
            try {
                checkAndExpireItemSessionUseCase()
                when (val result = claimRewardUseCase(
                    "playing-writing-game",
                    _uiState.value.hackExperience,
                    _uiState.value.superExperience
                )) {
                    is Result.Success -> {
                        syncUserSessionUseCase()
                        val xp = result.data.experienceEarned
                        val diamond = result.data.diamondEarned
                        _uiState.update { it.copy(isSubmitting = false, experienceEarned = xp, diamondEarned = diamond) }
                        val streak = result.data.streak
                        if (streak.status == "created" || streak.status == "extended") {
                            _navEvent.send(WritingNavEvent.ToStreak(streak.currentLength, xp, diamond, timeFormatted, _uiState.value.hackExperience, _uiState.value.superExperience))
                        } else {
                            _navEvent.send(WritingNavEvent.ToResult(xp, diamond, timeFormatted, _uiState.value.hackExperience, _uiState.value.superExperience))
                        }
                    }
                    else -> {
                        _uiState.update { it.copy(isSubmitting = false) }
                        _navEvent.send(WritingNavEvent.ToResult(15, 10, timeFormatted))
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSubmitting = false) }
                _navEvent.send(WritingNavEvent.ToResult(15, 10, timeFormatted))
            }
        }
    }

    fun getCompletionTimeFormatted(): String {
        val totalSeconds = (_uiState.value.completionTime / 1000).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    fun resetAnswerResult() {
        _uiState.update { it.copy(answerResult = AnswerResult.None) }
    }

    private fun updateSentenceStatus(sentenceId: Int, newStatus: String) {
        viewModelScope.launch {
            val sentence = _uiState.value.sentences.find { it.id == sentenceId } ?: return@launch
            
            updateSentenceUseCase(
                id = sentenceId,
                term = sentence.term,
                definition = sentence.definition,
                status = newStatus,
                mistakes = sentence.numberOfMistakes ?: 0
            )
        }
    }

    private fun incrementMistakes(sentence: Setence) {
        viewModelScope.launch {
            val currentMistakes = sentence.numberOfMistakes ?: 0
            
            updateSentenceUseCase(
                id = sentence.id,
                term = sentence.term,
                definition = sentence.definition,
                status = sentence.status,
                mistakes = currentMistakes + 1
            )
        }
    }

    private fun normalizeAnswer(answer: String): String {
        return answer.trim()
            .lowercase()
            .replace(Regex("[^a-z0-9\\s]"), "") // Bỏ dấu câu
            .replace(Regex("\\s+"), " ") // Normalize spaces
    }

    fun getProgress(): Float {
        val total = _uiState.value.sentences.size
        val current = _uiState.value.currentIndex + 1
        return if (total > 0) current.toFloat() / total.toFloat() else 0f
    }
    
    fun getTotalProgress(): String {
        val current = _uiState.value.currentIndex + 1
        val total = _uiState.value.sentences.size
        return "$current/$total"
    }

    fun resetState() {
        _uiState.value = WritingPracticeUiState()
    }
}
