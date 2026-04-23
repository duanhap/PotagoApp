package com.example.potago.presentation.screen.detailsentencepatternscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Setence
import com.example.potago.domain.usecase.GetSentencesByPatternUseCase
import com.example.potago.domain.usecase.DeleteSentenceUseCase
import com.example.potago.domain.usecase.UpdateSentenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ListOfDetailUiState(
    val isLoading: Boolean = false,
    val sentences: List<Setence> = emptyList(),
    val filteredSentences: List<Setence> = emptyList(),
    val error: String? = null,
    val selectedFilter: String = "all", // "all", "unknown", "known"
    val deleteSuccess: Boolean = false,
    val deleteError: String? = null
)

@HiltViewModel
class ListOfDetailViewModel @Inject constructor(
    private val getSentencesByPatternUseCase: GetSentencesByPatternUseCase,
    private val deleteSentenceUseCase: DeleteSentenceUseCase,
    private val updateSentenceUseCase: UpdateSentenceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListOfDetailUiState())
    val uiState: StateFlow<ListOfDetailUiState> = _uiState.asStateFlow()

    fun loadSentences(patternId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = getSentencesByPatternUseCase(patternId)) {
                is Result.Success -> {
                    val sentences = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            sentences = sentences,
                            filteredSentences = filterSentences(sentences, it.selectedFilter)
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

    fun filterByStatus(status: String) {
        _uiState.update {
            it.copy(
                selectedFilter = status,
                filteredSentences = filterSentences(it.sentences, status)
            )
        }
    }

    fun deleteSentence(sentenceId: Int) {
        viewModelScope.launch {
            when (val result = deleteSentenceUseCase(sentenceId)) {
                is Result.Success -> {
                    // Remove từ list
                    _uiState.update {
                        val updatedSentences = it.sentences.filter { s -> s.id != sentenceId }
                        it.copy(
                            sentences = updatedSentences,
                            filteredSentences = filterSentences(updatedSentences, it.selectedFilter),
                            deleteSuccess = true
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(deleteError = result.message ?: "Xóa câu thất bại")
                    }
                }
                else -> {}
            }
        }
    }

    fun updateSentenceStatus(sentenceId: Int, newStatus: String) {
        viewModelScope.launch {
            // Tìm câu cần cập nhật
            val sentence = _uiState.value.sentences.find { it.id == sentenceId } ?: return@launch
            
            when (val result = updateSentenceUseCase(
                id = sentenceId,
                term = sentence.term,
                definition = sentence.definition,
                status = newStatus,
                mistakes = sentence.numberOfMistakes ?: 0
            )) {
                is Result.Success -> {
                    // Cập nhật trong list
                    _uiState.update {
                        val updatedSentences = it.sentences.map { s ->
                            if (s.id == sentenceId) result.data else s
                        }
                        it.copy(
                            sentences = updatedSentences,
                            filteredSentences = filterSentences(updatedSentences, it.selectedFilter)
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(error = result.message ?: "Cập nhật trạng thái thất bại")
                    }
                }
                else -> {}
            }
        }
    }

    fun refreshSentences(patternId: Int) {
        loadSentences(patternId)
    }

    fun clearDeleteSuccess() {
        _uiState.update { it.copy(deleteSuccess = false) }
    }

    private fun filterSentences(sentences: List<Setence>, status: String): List<Setence> {
        return when (status) {
            "unknown" -> sentences.filter { it.status == "unknown" }
            "known" -> sentences.filter { it.status == "known" }
            else -> sentences
        }
    }
}
