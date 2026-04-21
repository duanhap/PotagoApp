package com.example.potago.presentation.screen.matchgame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.data.remote.dto.BestTimeDto
import com.example.potago.data.remote.dto.MatchCardDto
import com.example.potago.domain.model.Result
import com.example.potago.domain.repository.MatchGameRepository
import com.example.potago.domain.usecase.CheckAndExpireItemSessionUseCase
import com.example.potago.domain.usecase.ClaimRewardUseCase
import com.example.potago.domain.usecase.ObserveActiveItemSessionUseCase
import com.example.potago.domain.usecase.SyncUserSessionUseCase
import com.example.potago.presentation.navigation.Screen
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
    val showExitDialog: Boolean = false,
    val isSubmitting: Boolean = false, // đang gọi API sau khi hoàn thành
    // Bonus item states (for MatchResultScreen animation)
    val hackExperience: Boolean = false,
    val superExperience: Boolean = false
)

sealed class MatchGameNavEvent {
    data class ToStreak(val streakCount: Int, val completedTime: Double, val bestTime: Double, val bestDate: String, val wordSetId: Long, val wordSetName: String, val hackXp: Boolean, val superXp: Boolean) : MatchGameNavEvent()
    data class ToResult(val completedTime: Double, val bestTime: Double, val bestDate: String, val wordSetId: Long, val wordSetName: String, val hackXp: Boolean, val superXp: Boolean) : MatchGameNavEvent()
}

@HiltViewModel
class MatchGameViewModel @Inject constructor(
    private val repository: MatchGameRepository,
    private val claimRewardUseCase: ClaimRewardUseCase,
    private val syncUserSessionUseCase: SyncUserSessionUseCase,
    private val observeActiveItemSessionUseCase: ObserveActiveItemSessionUseCase,
    private val checkAndExpireItemSessionUseCase: CheckAndExpireItemSessionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MatchGameUiState())
    val uiState: StateFlow<MatchGameUiState> = _uiState.asStateFlow()

    private val _navEvent = Channel<MatchGameNavEvent>()
    val navEvent = _navEvent.receiveAsFlow()

    private var gameId: Int = 0
    private var wordSetId: Long = 0L
    private var wordSetName: String = ""
    private var selectedCard: MatchCard? = null
    private var timerJob: Job? = null
    private var startTime: Long = 0L

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

    fun startGame(wordSetId: Long, wordSetName: String = "") {
        this.wordSetId = wordSetId
        this.wordSetName = wordSetName
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
            _uiState.update { it.copy(isSubmitting = true) }
            val bestTime: BestTimeDto?
            when (val result = repository.submitResult(gameId, wordSetId, completedTime)) {
                is Result.Success -> {
                    bestTime = result.data?.bestTime
                    _uiState.update { it.copy(bestTime = bestTime) }
                }
                else -> bestTime = null
            }

            // Claim reward sau khi submit xong
            claimAndNavigate(
                completedTime = completedTime,
                bestTime = bestTime?.bestTime ?: 0.0,
                bestDate = bestTime?.date ?: ""
            )
        }
    }

    private suspend fun claimAndNavigate(completedTime: Double, bestTime: Double, bestDate: String) {
        val state = _uiState.value
        try {
            checkAndExpireItemSessionUseCase()
            when (val result = claimRewardUseCase(
                action = "playing-match-game",
                hackExperience = state.hackExperience,
                superExperience = state.superExperience
            )) {
                is Result.Success -> {
                    syncUserSessionUseCase()
                    val streak = result.data.streak
                    if (streak.status == "created" || streak.status == "extended") {
                        _navEvent.send(MatchGameNavEvent.ToStreak(
                            streakCount = streak.currentLength,
                            completedTime = completedTime,
                            bestTime = bestTime,
                            bestDate = bestDate,
                            wordSetId = wordSetId,
                            wordSetName = wordSetName,
                            hackXp = state.hackExperience,
                            superXp = state.superExperience
                        ))
                    } else {
                        _navEvent.send(MatchGameNavEvent.ToResult(
                            completedTime = completedTime,
                            bestTime = bestTime,
                            bestDate = bestDate,
                            wordSetId = wordSetId,
                            wordSetName = wordSetName,
                            hackXp = state.hackExperience,
                            superXp = state.superExperience
                        ))
                    }
                }
                else -> {
                    _navEvent.send(MatchGameNavEvent.ToResult(
                        completedTime = completedTime,
                        bestTime = bestTime,
                        bestDate = bestDate,
                        wordSetId = wordSetId,
                        wordSetName = wordSetName,
                        hackXp = state.hackExperience,
                        superXp = state.superExperience
                    ))
                }
            }
        } catch (e: Exception) {
            _navEvent.send(MatchGameNavEvent.ToResult(
                completedTime = completedTime,
                bestTime = bestTime,
                bestDate = bestDate,
                wordSetId = wordSetId,
                wordSetName = wordSetName,
                hackXp = state.hackExperience,
                superXp = state.superExperience
            ))
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
