package com.example.potago.presentation.screen.wordordering

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Result
import com.example.potago.domain.usecase.CheckAndExpireItemSessionUseCase
import com.example.potago.domain.usecase.ObserveActiveItemSessionUseCase
import com.example.potago.domain.usecase.StartWordOrderingGameUseCase
import com.example.potago.domain.usecase.SubmitWordOrderingResultUseCase
import com.example.potago.domain.usecase.SyncUserSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrderingSentence(
    val id: Int,
    val term: String,
    val definition: String
)

enum class CheckResult { NONE, CORRECT, WRONG }

data class PoolChip(
    val id: Int,
    val word: String,
    val isSelected: Boolean = false
)

data class WordOrderingUiState(
    val sentences: List<OrderingSentence> = emptyList(),  // queue hiện tại (bao gồm câu bị đẩy lại)
    val currentIndex: Int = 0,
    val poolChips: List<PoolChip> = emptyList(),
    val answerChipIds: List<Int> = emptyList(),
    val checkResult: CheckResult = CheckResult.NONE,
    val isFinished: Boolean = false,
    val correctCount: Int = 0,
    val totalOriginal: Int = 0,          // tổng câu gốc (không đổi)
    val progress: Float = 0f,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val gameId: Int = 0,
    val patternId: Int = 0,
    val hackExperience: Boolean = false,
    val superExperience: Boolean = false,
    val elapsedSeconds: Double = 0.0
) {
    val selectedWords: List<String>
        get() = answerChipIds.mapNotNull { id -> poolChips.find { it.id == id }?.word }
}

sealed class WordOrderingNavEvent {
    data class ToStreak(val streakCount: Int, val correctCount: Int, val totalCount: Int, val completedTime: Double, val xpEarned: Int, val diamondEarned: Int, val hackXp: Boolean = false, val superXp: Boolean = false) : WordOrderingNavEvent()
    data class ToResult(val correctCount: Int, val totalCount: Int, val completedTime: Double, val xpEarned: Int, val diamondEarned: Int, val hackXp: Boolean = false, val superXp: Boolean = false) : WordOrderingNavEvent()
}

@HiltViewModel
class WordOrderingViewModel @Inject constructor(
    private val startGameUseCase: StartWordOrderingGameUseCase,
    private val submitResultUseCase: SubmitWordOrderingResultUseCase,
    private val syncUserSessionUseCase: SyncUserSessionUseCase,
    private val observeActiveItemSessionUseCase: ObserveActiveItemSessionUseCase,
    private val checkAndExpireItemSessionUseCase: CheckAndExpireItemSessionUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(WordOrderingUiState())
    val uiState: StateFlow<WordOrderingUiState> = _uiState.asStateFlow()

    private val _navEvent = Channel<WordOrderingNavEvent>()
    val navEvent = _navEvent.receiveAsFlow()

    private val patternId: Int = savedStateHandle.get<Int>("patternId") ?: 0
    private val correctSentenceIds = mutableListOf<Int>()
    private val wrongSentenceIds = mutableListOf<Int>()   // chỉ lưu lần sai ĐẦU TIÊN của mỗi câu
    private val alreadyWronged = mutableSetOf<Int>()      // tránh đếm sai nhiều lần cùng 1 câu
    private var timerJob: Job? = null
    private var startTimeMs: Long = 0L

    init {
        observeActiveItems()
        if (patternId > 0) loadGame(patternId)
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

    private fun loadGame(patternId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, patternId = patternId) }
            when (val result = startGameUseCase(patternId)) {
                is Result.Success -> {
                    val (gameId, sentences) = result.data
                    val orderingSentences = sentences.map {
                        OrderingSentence(it.id, it.term, it.definition)
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            gameId = gameId,
                            sentences = orderingSentences,
                            totalOriginal = orderingSentences.size
                        )
                    }
                    loadCurrentSentence(0, orderingSentences)
                    startTimer()
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                else -> {}
            }
        }
    }

    private fun loadCurrentSentence(
        index: Int,
        sentences: List<OrderingSentence> = _uiState.value.sentences
    ) {
        if (index >= sentences.size) {
            submitGameResult()
            return
        }
        val words = sentences[index].term
            .split(" ")
            .map { it.trim().trimEnd('?', '.', ',', '!') }
            .filter { it.isNotBlank() }
            .shuffled()

        val pool = words.mapIndexed { i, w -> PoolChip(id = i, word = w) }
        // Progress dựa trên số câu đúng / tổng câu gốc
        val prog = _uiState.value.correctCount.toFloat() / _uiState.value.totalOriginal.coerceAtLeast(1)

        _uiState.update {
            it.copy(
                currentIndex = index,
                poolChips = pool,
                answerChipIds = emptyList(),
                checkResult = CheckResult.NONE,
                progress = prog
            )
        }
    }

    fun onPoolChipTap(chipId: Int) {
        val state = _uiState.value
        if (state.checkResult != CheckResult.NONE) return
        state.poolChips.find { it.id == chipId && !it.isSelected } ?: return
        _uiState.update {
            it.copy(
                poolChips = it.poolChips.map { c -> if (c.id == chipId) c.copy(isSelected = true) else c },
                answerChipIds = it.answerChipIds + chipId
            )
        }
    }

    fun onAnswerChipTap(chipId: Int) {
        if (_uiState.value.checkResult != CheckResult.NONE) return
        _uiState.update {
            it.copy(
                poolChips = it.poolChips.map { c -> if (c.id == chipId) c.copy(isSelected = false) else c },
                answerChipIds = it.answerChipIds - chipId
            )
        }
    }

    fun checkAnswer() {
        val state = _uiState.value
        val current = state.sentences.getOrNull(state.currentIndex) ?: return
        val answer = state.selectedWords.joinToString(" ")
        val correct = current.term
            .split(" ")
            .map { it.trim().trimEnd('?', '.', ',', '!') }
            .filter { it.isNotBlank() }
            .joinToString(" ")

        val isCorrect = answer.equals(correct, ignoreCase = true)

        if (isCorrect) {
            correctSentenceIds.add(current.id)
        } else {
            // Chỉ ghi nhận sai lần đầu tiên
            if (alreadyWronged.add(current.id)) {
                wrongSentenceIds.add(current.id)
            }
        }

        _uiState.update {
            val newCorrectCount = if (isCorrect) it.correctCount + 1 else it.correctCount
            it.copy(
                checkResult = if (isCorrect) CheckResult.CORRECT else CheckResult.WRONG,
                correctCount = newCorrectCount,
                progress = newCorrectCount.toFloat() / it.totalOriginal.coerceAtLeast(1)
            )
        }
    }

    fun nextSentence() {
        val state = _uiState.value
        val current = state.sentences.getOrNull(state.currentIndex) ?: return

        if (state.checkResult == CheckResult.WRONG) {
            // Đẩy câu sai vào cuối queue
            val newQueue = state.sentences.toMutableList()
            newQueue.removeAt(state.currentIndex)
            newQueue.add(current)
            _uiState.update { it.copy(sentences = newQueue) }
            // index giữ nguyên → sẽ load câu tiếp theo (vốn là câu kế)
            loadCurrentSentence(state.currentIndex, newQueue)
        } else {
            loadCurrentSentence(state.currentIndex + 1)
        }
    }

    private fun startTimer() {
        startTimeMs = System.currentTimeMillis()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(100)
                val elapsed = (System.currentTimeMillis() - startTimeMs) / 1000.0
                _uiState.update { it.copy(elapsedSeconds = elapsed) }
            }
        }
    }

    private fun submitGameResult() {
        timerJob?.cancel()
        val completedTime = (System.currentTimeMillis() - startTimeMs) / 1000.0

        viewModelScope.launch {
            val state = _uiState.value
            _uiState.update { it.copy(isSubmitting = true) }

            when (val result = submitResultUseCase(
                gameId = state.gameId,
                patternId = state.patternId,
                correctSentenceIds = correctSentenceIds,
                wrongSentenceIds = wrongSentenceIds,
                hackExperience = state.hackExperience,
                superExperience = state.superExperience
            )) {
                is Result.Success -> {
                    syncUserSessionUseCase()
                    val streak = result.data.streak
                    val correctCount = result.data.correctCount
                    val totalCount = result.data.totalCount
                    val xpEarned = result.data.experienceEarned
                    val diamondEarned = result.data.diamondEarned
                    _uiState.update { it.copy(isSubmitting = false, isFinished = true) }

                    if (streak.status == "created" || streak.status == "extended") {
                        _navEvent.send(WordOrderingNavEvent.ToStreak(streak.currentLength, correctCount, totalCount, completedTime, xpEarned, diamondEarned, state.hackExperience, state.superExperience))
                    } else {
                        _navEvent.send(WordOrderingNavEvent.ToResult(correctCount, totalCount, completedTime, xpEarned, diamondEarned, state.hackExperience, state.superExperience))
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isSubmitting = false, isFinished = true) }
                    _navEvent.send(WordOrderingNavEvent.ToResult(state.correctCount, state.totalOriginal, completedTime, 0, 0))
                }
                else -> {}
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
