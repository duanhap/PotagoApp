package com.example.potago.presentation.screen.wordordering

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class OrderingSentence(
    val id: Int,
    val term: String,
    val definition: String
)

enum class CheckResult { NONE, CORRECT, WRONG }

// Each chip in the pool keeps its fixed position; isSelected = grayed out placeholder
data class PoolChip(
    val id: Int,        // unique index to distinguish duplicates
    val word: String,
    val isSelected: Boolean = false
)

data class WordOrderingUiState(
    val sentences: List<OrderingSentence> = emptyList(),
    val currentIndex: Int = 0,
    // Pool: fixed positions, selected ones show as gray placeholder
    val poolChips: List<PoolChip> = emptyList(),
    // Answer bar: ordered list of chip ids
    val answerChipIds: List<Int> = emptyList(),
    val checkResult: CheckResult = CheckResult.NONE,
    val isFinished: Boolean = false,
    val correctCount: Int = 0,
    val progress: Float = 0f
) {
    // Convenience: words in answer bar in order
    val selectedWords: List<String>
        get() = answerChipIds.mapNotNull { id -> poolChips.find { it.id == id }?.word }
}

@HiltViewModel
class WordOrderingViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(WordOrderingUiState())
    val uiState: StateFlow<WordOrderingUiState> = _uiState.asStateFlow()

    private val mockSentences = listOf(
        OrderingSentence(1, "Where is the nearest station?", "Ga gần nhất ở đâu?"),
        OrderingSentence(2, "Can you help me please?", "Bạn có thể giúp tôi không?"),
        OrderingSentence(3, "I would like a coffee.", "Tôi muốn một ly cà phê."),
        OrderingSentence(4, "How much does it cost?", "Cái này giá bao nhiêu?"),
        OrderingSentence(5, "What time does it open?", "Mấy giờ thì mở cửa?")
    )

    init { loadSentences() }

    private fun loadSentences() {
        val selected = mockSentences.shuffled().take(5)
        _uiState.update { it.copy(sentences = selected) }
        loadCurrentSentence(0, selected)
    }

    private fun loadCurrentSentence(
        index: Int,
        sentences: List<OrderingSentence> = _uiState.value.sentences
    ) {
        if (index >= sentences.size) {
            _uiState.update { it.copy(isFinished = true) }
            return
        }
        val words = sentences[index].term
            .split(" ")
            .map { it.trim().trimEnd('?', '.', ',', '!') }
            .filter { it.isNotBlank() }
            .shuffled()

        val pool = words.mapIndexed { i, w -> PoolChip(id = i, word = w) }
        val prog = if (sentences.isEmpty()) 0f else index.toFloat() / sentences.size

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

    // Tap a chip in the pool → move to answer bar, mark as selected (gray)
    fun onPoolChipTap(chipId: Int) {
        val state = _uiState.value
        if (state.checkResult != CheckResult.NONE) return
        val chip = state.poolChips.find { it.id == chipId && !it.isSelected } ?: return

        _uiState.update {
            it.copy(
                poolChips = it.poolChips.map { c ->
                    if (c.id == chipId) c.copy(isSelected = true) else c
                },
                answerChipIds = it.answerChipIds + chipId
            )
        }
    }

    // Tap a chip in the answer bar → return to original pool position, unmark
    fun onAnswerChipTap(chipId: Int) {
        val state = _uiState.value
        if (state.checkResult != CheckResult.NONE) return

        _uiState.update {
            it.copy(
                poolChips = it.poolChips.map { c ->
                    if (c.id == chipId) c.copy(isSelected = false) else c
                },
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
        _uiState.update {
            it.copy(
                checkResult = if (isCorrect) CheckResult.CORRECT else CheckResult.WRONG,
                correctCount = if (isCorrect) it.correctCount + 1 else it.correctCount
            )
        }
    }

    fun nextSentence() {
        loadCurrentSentence(_uiState.value.currentIndex + 1)
    }
}
