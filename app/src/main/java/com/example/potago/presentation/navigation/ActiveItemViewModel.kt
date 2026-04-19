package com.example.potago.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.potago.domain.model.ActiveItemSession
import com.example.potago.domain.usecase.ObserveActiveItemSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ActiveItemViewModel @Inject constructor(
    observeActiveItemSessionUseCase: ObserveActiveItemSessionUseCase
) : ViewModel() {

    val activeSession: StateFlow<ActiveItemSession?> = observeActiveItemSessionUseCase()
        .map { session -> session?.takeIf { it.isActive } }
        .stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5000), null)
}