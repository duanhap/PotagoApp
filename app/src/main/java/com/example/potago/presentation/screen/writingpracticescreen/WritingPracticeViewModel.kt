package com.example.potago.presentation.screen.writingpracticescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Setence
import com.example.potago.domain.usecase.GetSentencesByPatternUseCase
import com.example.potago.domain.usecase.UpdateSentenceUseCase
import com.example.potago.domain.usecase.ClaimRewardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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
    val diamondEarned: Int = 10
)

@HiltViewModel
class WritingPracticeViewModel @Inject constructor(
    private val getSentencesByPatternUseCase: GetSentencesByPatternUseCase,
    private val updateSentenceUseCase: UpdateSentenceUseCase,
    private val claimRewardUseCase: ClaimRewardUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WritingPracticeUiState())
    val uiState: StateFlow<WritingPracticeUiState> = _uiState.asStateFlow()

    fun loadSentences(patternId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, startTime = System.currentTimeMillis()) }
            when (val result = getSentencesByPatternUseCase(patternId)) {
                is Result.Success -> {
                    // Chỉ lấy câu chưa thuộc (status = "unknown")
                    val unknownSentences = result.data.filter { it.status == "unknown" }
                    
                    if (unknownSentences.isEmpty()) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isCompleted = true,
                                completionTime = 0L,
                                error = "Bạn đã hoàn thành tất cả câu!"
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                sentences = unknownSentences,
                                currentSentence = unknownSentences.firstOrNull(),
                                currentIndex = 0
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
    }

    fun checkAnswer(userAnswer: String) {
        val currentSentence = _uiState.value.currentSentence ?: return
        val correctAnswer = currentSentence.term
        
        // Normalize và so sánh
        val isCorrect = normalizeAnswer(userAnswer) == normalizeAnswer(correctAnswer)
        
        if (isCorrect) {
            _uiState.update { it.copy(answerResult = AnswerResult.Correct) }
            // Cập nhật status thành "known"
            updateSentenceStatus(currentSentence.id, "known")
        } else {
            _uiState.update { 
                it.copy(answerResult = AnswerResult.Incorrect(correctAnswer)) 
            }
            // Tăng số lần sai
            incrementMistakes(currentSentence)
        }
    }

    fun moveToNextSentence() {
        val currentIndex = _uiState.value.currentIndex
        val sentences = _uiState.value.sentences
        
        if (currentIndex + 1 < sentences.size) {
            _uiState.update {
                it.copy(
                    currentIndex = currentIndex + 1,
                    currentSentence = sentences[currentIndex + 1],
                    answerResult = AnswerResult.None
                )
            }
        } else {
            // Hoàn thành tất cả câu - tính thời gian
            val completionTime = System.currentTimeMillis() - _uiState.value.startTime
            _uiState.update {
                it.copy(
                    isCompleted = true,
                    completionTime = completionTime,
                    answerResult = AnswerResult.None
                )
            }
            // Claim rewards
            claimRewards()
        }
    }

    private fun claimRewards() {
        viewModelScope.launch {
            when (val result = claimRewardUseCase("playing-writing-game", false, false)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            experienceEarned = result.data.experienceEarned,
                            diamondEarned = result.data.diamondEarned
                        )
                    }
                }
                is Result.Error -> {
                    // Nếu API lỗi, giữ giá trị mặc định (15 XP, 10 Diamond)
                    // Không cần thông báo lỗi vì người dùng vẫn thấy phần thưởng
                }
                else -> {}
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

    fun resetState() {
        _uiState.value = WritingPracticeUiState()
    }
}
