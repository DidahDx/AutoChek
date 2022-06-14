package com.github.didahdx.autochek.ui.carDetails

/**
 * @author Daniel Didah on 6/14/22
 */
sealed class UiState{
    data class Loading(var isLoading:Boolean): UiState()
    data class Error(var error: String): UiState()
    object Success : UiState()
}
