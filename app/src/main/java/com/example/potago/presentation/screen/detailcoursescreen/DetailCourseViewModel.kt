package com.example.potago.presentation.screen.detailcoursescreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.Result
import com.example.potago.domain.usecase.DeleteWordSetUseCase
import com.example.potago.presentation.navigation.Screen
import com.example.potago.presentation.screen.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailCourseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val deleteWordSetUseCase: DeleteWordSetUseCase
) : ViewModel() {

    private val wordSetId: Long = savedStateHandle.get<Long>("wordSetId") ?: 0L

    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    fun deleteWordSet() {
        viewModelScope.launch {
            if (wordSetId == 0L) {
                _uiEvent.send(UiEvent.ShowSnackbar("Không xác định được học phần"))
                return@launch
            }
            when (val result = deleteWordSetUseCase(wordSetId)) {
                is Result.Success -> {
                    _uiEvent.send(
                        UiEvent.Navigate(
                            route = Screen.Library.route,
                            popUpTo = Screen.Library.route,
                            inclusive = false
                        )
                    )
                }
                is Result.Error -> _uiEvent.send(UiEvent.ShowSnackbar(result.message))
                else -> {}
            }
        }
    }
}
