package com.example.potago.presentation.screen

/**
 * A generic class that holds a value with its loading status.
 * @param <T> The type of data held by this state.
 */
sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T? = null) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
