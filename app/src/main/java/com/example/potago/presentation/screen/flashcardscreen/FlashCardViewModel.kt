package com.example.potago.presentation.screen.flashcardscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.data.local.WordSetDataStore
import com.example.potago.data.local.WordSetProgress
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Word
import com.example.potago.domain.usecase.GetFlashcardsUseCase
import com.example.potago.domain.usecase.GetWordSetByIdUseCase
import com.example.potago.domain.usecase.UpdateWordStatusUseCase
import com.example.potago.presentation.screen.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FlashCardUiState(
    val words: List<Word> = emptyList(),
    val currentIndex: Int = 0,
    val totalWords: Int = 0,
    val mode: String = "normal",
    val filter: String = "all",
    val uiState: UiState<Unit> = UiState.Loading,
    val history: List<Int> = emptyList(),
    val isRandomSheetVisible: Boolean = false,
    val termLangCode: String = "en" // Default to English
)

enum class StatusFlashCard(val displayName: String, val filter: String) {
    All("Tất cả", "all"),
    UNKNOWN("Không nhớ", "unknown"),
    KNOWN("Đã nhớ", "known")
}

@HiltViewModel
class FlashCardViewModel @Inject constructor(
    private val getFlashcardsUseCase: GetFlashcardsUseCase,
    private val getWordSetByIdUseCase: GetWordSetByIdUseCase,
    private val updateWordStatusUseCase: UpdateWordStatusUseCase,
    private val dataStore: WordSetDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(FlashCardUiState())
    val uiState: StateFlow<FlashCardUiState> = _uiState.asStateFlow()

    private var currentWordSetId: Long = 0

    fun init(wordSetId: Long) {
        if (currentWordSetId == wordSetId) return
        currentWordSetId = wordSetId
        
        fetchWordSetDetails(wordSetId)
        
        viewModelScope.launch {
            val progress = dataStore.getProgress(wordSetId).firstOrNull()
            if (progress != null) {
                val savedMode = progress.mode ?: "normal"
                val savedFilter = progress.filter ?: "all"
                
                _uiState.update { it.copy(mode = savedMode, filter = savedFilter) }
                loadFlashcards(progress.currentWordId)
            } else {
                saveProgress()
                loadFlashcards()
            }
        }
    }

    private fun fetchWordSetDetails(wordSetId: Long) {
        viewModelScope.launch {
            when (val result = getWordSetByIdUseCase(wordSetId)) {
                is Result.Success -> {
                    _uiState.update { it.copy(termLangCode = result.data.termLanguageCode) }
                }
                is Result.Error -> {
                    // Handle error if needed
                }
                else -> {}
            }
        }
    }

    fun loadFlashcards(startWordId: Long? = null) {
        viewModelScope.launch {
            val isInitial = _uiState.value.words.isEmpty()
            _uiState.update { it.copy(uiState = if (isInitial) UiState.Loading else it.uiState) }
            
            val result = getFlashcardsUseCase(
                wordSetId = currentWordSetId,
                mode = _uiState.value.mode,
                currentWordId = startWordId,
                size = 20,
                filter = _uiState.value.filter
            )

            when (result) {
                is Result.Success -> {
                    val newWords = result.data.words
                    val total = result.data.total
                    _uiState.update { currentState ->
                        val updatedWords = if (isInitial || startWordId == null || newWords.firstOrNull()?.flashcardOrder == 1) {
                            newWords
                        } else {
                            currentState.words + newWords.drop(1)
                        }
                        
                        val newIndex = if (isInitial || startWordId == null || newWords.firstOrNull()?.flashcardOrder == 1) {
                            0
                        } else {
                            currentState.currentIndex + 1
                        }
                        val newHistory = if (isInitial || startWordId == null || newWords.firstOrNull()?.flashcardOrder == 1) {
                            emptyList()
                        } else {
                            currentState.history + currentState.currentIndex
                        }

                        currentState.copy(
                            words = updatedWords,
                            totalWords = total,
                            uiState = UiState.Success(Unit),
                            currentIndex = newIndex,
                            history = newHistory
                        )
                    }
                    saveProgress()
                }
                is Result.Error -> {
                    _uiState.update { it.copy(uiState = UiState.Error(result.message), totalWords = 0, words = emptyList()) }
                }
                else -> {}
            }
        }
    }

    fun onFilterChanged(newFilter: String) {
        if (_uiState.value.filter == newFilter) return
        
        _uiState.update { 
            it.copy(
                filter = newFilter,
                words = emptyList(),
                currentIndex = 0,
                history = emptyList()
            ) 
        }
        loadFlashcards()
        saveProgress()
    }

    fun onNext(status: String) {
        val currentState = _uiState.value
        val currentWord = currentState.words.getOrNull(currentState.currentIndex) ?: return

        viewModelScope.launch {
            updateWordStatusUseCase(currentWord.id, status)
        }

        val nextIndex = currentState.currentIndex + 1
        if (nextIndex < currentState.words.size) {
            _uiState.update { 
                it.copy(
                    currentIndex = nextIndex,
                    history = it.history + currentState.currentIndex
                )
            }
            saveProgress()
        } else {
            loadFlashcards(currentWord.id)
        }
    }

    fun onPrevious() {
        val currentState = _uiState.value
        if (currentState.history.isNotEmpty()) {
            val prevIndex = currentState.history.last()
            _uiState.update { 
                it.copy(
                    currentIndex = prevIndex,
                    history = it.history.dropLast(1)
                )
            }
            saveProgress()
        }
    }

    fun toggleMode() {
        if (_uiState.value.mode == "normal") {
            _uiState.update { it.copy(isRandomSheetVisible = true) }
        } else {
            setMode("normal")
        }
    }

    fun confirmRandomMode() {
        _uiState.update { it.copy(isRandomSheetVisible = false) }
        setMode("random")
    }

    fun dismissRandomSheet() {
        _uiState.update { it.copy(isRandomSheetVisible = false) }
    }

    private fun setMode(mode: String) {
        _uiState.update { it.copy(mode = mode, words = emptyList(), currentIndex = 0, history = emptyList()) }
        loadFlashcards()
        saveProgress()
    }

    private fun saveProgress() {
        val currentState = _uiState.value
        val currentWordId = currentState.words.getOrNull(currentState.currentIndex)?.id
        viewModelScope.launch {
            dataStore.saveProgress(
                WordSetProgress(
                    wordSetId = currentWordSetId,
                    mode = currentState.mode,
                    currentWordId = currentWordId,
                    filter = currentState.filter
                )
            )
        }
    }
}
