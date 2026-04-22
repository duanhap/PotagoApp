package com.example.potago.presentation.screen.editcoursescreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Result
import com.example.potago.domain.usecase.GetWordSetByIdUseCase
import com.example.potago.domain.usecase.UpdateWordSetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditCourseUiState(
    val title: String = "",
    val description: String = "",
    val termLangCode: String = "en",
    val defLangCode: String = "vi",
    val termLanguageLabel: String = "English",
    val definitionLanguageLabel: String = "Tiếng Việt",
    val isPublic: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class EditCourseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getWordSetByIdUseCase: GetWordSetByIdUseCase,
    private val updateWordSetUseCase: UpdateWordSetUseCase
) : ViewModel() {

    private val wordSetId: Long = savedStateHandle["wordSetId"] ?: 0L

    private val _uiState = MutableStateFlow(EditCourseUiState(
        title = savedStateHandle["wordSetName"] ?: ""
    ))
    val uiState: StateFlow<EditCourseUiState> = _uiState.asStateFlow()

    init {
        loadWordSet()
    }

    private fun loadWordSet() {
        if (wordSetId == 0L) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = getWordSetByIdUseCase(wordSetId)
            if (result is Result.Success) {
                val wordset = result.data
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        title = wordset.name,
                        description = wordset.description ?: "",
                        termLangCode = wordset.termLanguageCode,
                        defLangCode = wordset.definitionLanguageCode,
                        isPublic = wordset.isPublic ?: false,
                        termLanguageLabel = mapCodeToLang(wordset.termLanguageCode),
                        definitionLanguageLabel = mapCodeToLang(wordset.definitionLanguageCode)
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Failed to load word set") }
            }
        }
    }

    private fun mapCodeToLang(code: String): String {
        return when (code) {
            "en" -> "English"
            "vi" -> "Tiếng Việt"
            "ja" -> "日本語"
            "zh" -> "中文"
            else -> code
        }
    }

    fun onTitleChange(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }

    fun onDescriptionChange(newDesc: String) {
        _uiState.update { it.copy(description = newDesc) }
    }

    fun onTermLangChange(newCode: String) {
        _uiState.update { it.copy(
            termLangCode = newCode,
            termLanguageLabel = mapCodeToLang(newCode)
        ) }
    }

    fun onDefLangChange(newCode: String) {
        _uiState.update { it.copy(
            defLangCode = newCode,
            definitionLanguageLabel = mapCodeToLang(newCode)
        ) }
    }

    fun saveChanges() {
        if (wordSetId == 0L) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val currentState = _uiState.value
            val result = updateWordSetUseCase(
                wordSetId = wordSetId,
                defLangCode = currentState.defLangCode,
                description = currentState.description,
                isPublic = currentState.isPublic,
                name = currentState.title,
                termLangCode = currentState.termLangCode
            )
            if (result is Result.Success) {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } else if (result is Result.Error) {
                _uiState.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }
}
