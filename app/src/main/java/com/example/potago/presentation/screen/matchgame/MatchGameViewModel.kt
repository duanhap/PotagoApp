package com.example.potago.presentation.screen.matchgame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.data.remote.dto.BestTimeDto
import com.example.potago.data.remote.dto.MatchCardDto
import com.example.potago.domain.model.Result
import com.example.potago.domain.repository.MatchGameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class CardState { IDLE, SELECTED, MATCHED_VISIBLE, MATCHED, WRONG }

data class MatchCard(
    val cardId: String,
    val pairId: Long,
    val content: String,
    val type: String,
    val state: CardState = CardState.IDLE
)

data class MatchGameUiState(
    val isLoading: Boolean = true,
    val cards: List<MatchCard> = emptyList(),
    val elapsedSeconds: Double = 0.0,
    val isFinished: Boolean = false,
    val completedTime: Double = 0.0,
    val bestTime: BestTimeDto? = null,
    val error: String? = null,
    val showExitDialog: Boolean = false
)

@HiltViewModel
class MatchGameViewModel @Inject constructor(
    private val repository: MatchGameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MatchGameUiState())
    val uiState: StateFlow<MatchGameUiState> = _uiState.asStateFlow()

    private var gameId: Int = 0
    private var wordSetId: Long = 0L
    private var selectedCard: MatchCard? = null
    private var timerJob: Job? = null
    private var startTime: Long = 0L

    fun startGame(wordSetId: Long) {
        this.wordSetId = wordSetId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.startGame(wordSetId)) {
                is Result.Success -> {
                    val (id, cards) = result.data!!
                    gameId = id
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            cards = cards.map { dto -> dto.toMatchCard() }
                        )
                    }
                    startTimer()
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                else -> {}
            }
        }
    }

    private fun startTimer() {
        startTime = System.currentTimeMillis()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(100)
                val elapsed = (System.currentTimeMillis() - startTime) / 1000.0
                _uiState.update { it.copy(elapsedSeconds = elapsed) }
            }
        }
    }

    fun onCardTap(card: MatchCard) {
        val current = _uiState.value
        if (card.state != CardState.IDLE) return

        val cards = current.cards.toMutableList()
        val idx = cards.indexOfFirst { it.cardId == card.cardId }
        if (idx == -1) return

        if (selectedCard == null) {
            cards[idx] = card.copy(state = CardState.SELECTED)
            selectedCard = cards[idx]
            _uiState.update { it.copy(cards = cards) }
        } else {
            val prev = selectedCard!!
            val prevIdx = cards.indexOfFirst { it.cardId == prev.cardId }

            if (prev.pairId == card.pairId && prev.type != card.type) {
                // Correct match — show green first, then hide after delay
                cards[prevIdx] = prev.copy(state = CardState.MATCHED_VISIBLE)
                cards[idx] = card.copy(state = CardState.MATCHED_VISIBLE)
                selectedCard = null
                _uiState.update { it.copy(cards = cards) }
                viewModelScope.launch {
                    delay(600)
                    val updated = _uiState.value.cards.toMutableList()
                    updated.replaceAll { c ->
                        if (c.state == CardState.MATCHED_VISIBLE) c.copy(state = CardState.MATCHED) else c
                    }
                    _uiState.update { it.copy(cards = updated) }
                    checkFinished(updated)
                }
            } else {
                // Wrong match
                cards[prevIdx] = prev.copy(state = CardState.WRONG)
                cards[idx] = card.copy(state = CardState.WRONG)
                selectedCard = null
                _uiState.update { it.copy(cards = cards) }
                viewModelScope.launch {
                    delay(500)
                    val reset = _uiState.value.cards.toMutableList()
                    reset.replaceAll { c ->
                        if (c.state == CardState.WRONG) c.copy(state = CardState.IDLE) else c
                    }
                    _uiState.update { it.copy(cards = reset) }
                }
            }
        }
    }

    private fun checkFinished(cards: List<MatchCard>) {
        if (cards.all { it.state == CardState.MATCHED }) {
            timerJob?.cancel()
            val elapsed = (System.currentTimeMillis() - startTime) / 1000.0
            _uiState.update { it.copy(completedTime = elapsed) }
            submitResult(elapsed)
        }
    }

    private fun submitResult(completedTime: Double) {
        viewModelScope.launch {
            when (val result = repository.submitResult(gameId, wordSetId, completedTime)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            bestTime = result.data?.bestTime,
                            isFinished = true  // navigate only after API returns
                        )
                    }
                }
                else -> {
                    // Navigate even if API fails, just without best time
                    _uiState.update { it.copy(isFinished = true) }
                }
            }
        }
    }

    fun showExitDialog() = _uiState.update { it.copy(showExitDialog = true) }
    fun dismissExitDialog() = _uiState.update { it.copy(showExitDialog = false) }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

private fun MatchCardDto.toMatchCard() = MatchCard(
    cardId = cardId,
    pairId = pairId,
    content = content,
    type = type
)
