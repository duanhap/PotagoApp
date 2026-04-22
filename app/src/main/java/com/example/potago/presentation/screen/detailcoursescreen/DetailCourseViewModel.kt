package com.example.potago.presentation.screen.detailcoursescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.WordSet
import com.example.potago.domain.usecase.DeleteWordSetUseCase
import com.example.potago.domain.usecase.GetWordSetByIdUseCase
import com.example.potago.domain.usecase.GetWordsByWordSetIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

data class DetailCourseUiState(
    val wordSet: WordSet? = null,
    val isLoading: Boolean = false,
    val isDeleted: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DetailCourseViewModel @Inject constructor(
    private val getWordSetByIdUseCase: GetWordSetByIdUseCase,
    private val getWordsByWordSetIdUseCase: GetWordsByWordSetIdUseCase,
    private val deleteWordSetUseCase: DeleteWordSetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailCourseUiState())
    val uiState: StateFlow<DetailCourseUiState> = _uiState.asStateFlow()

    fun loadWordSet(wordSetId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Fetch word set metadata and word list in parallel
            val wordSetDeferred = async { getWordSetByIdUseCase(wordSetId) }
            val wordsDeferred = async { getWordsByWordSetIdUseCase(wordSetId) }

            val wordSetResult = wordSetDeferred.await()
            val wordsResult = wordsDeferred.await()

            if (wordSetResult is Result.Error) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = wordSetResult.message
                )
                return@launch
            }

            val wordSet = (wordSetResult as Result.Success).data
            // Use the real list size when the by-id endpoint returns 0
            val realCount = if ((wordSet.amountOfWords ?: 0) > 0) {
                wordSet.amountOfWords
            } else {
                (wordsResult as? Result.Success)?.data?.size
            }

            _uiState.value = _uiState.value.copy(
                wordSet = wordSet.copy(amountOfWords = realCount),
                isLoading = false
            )
        }
    }

    fun deleteWordSet(wordSetId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = deleteWordSetUseCase(wordSetId)) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isDeleted = true
                )
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.message
                )
                else -> {}
            }
        }
    }

    /** Formats ISO-8601 createdAt → "Tháng M năm YYYY" */
    fun formatCreatedAt(createdAt: String): String {
        return try {
            val dt = OffsetDateTime.parse(createdAt)
            val formatter = DateTimeFormatter.ofPattern("'Tháng' M 'năm' yyyy", Locale("vi"))
            dt.format(formatter)
        } catch (e: Exception) {
            createdAt
        }
    }
}
