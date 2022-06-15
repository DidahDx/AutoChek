package com.github.didahdx.autochek.ui.carDetails

/**
 * @author Daniel Didah on 6/14/22
 */
sealed class UiState{
    object Loading : UiState()
    data class Error(var error: String): UiState()
    object Success : UiState()
}
