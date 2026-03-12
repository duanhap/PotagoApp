package com.example.potago.presentation.screen

sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
    data class Navigate(
        val route: String,
        val popUpTo: String? = null,
        val inclusive: Boolean = false
    ) : UiEvent()
}