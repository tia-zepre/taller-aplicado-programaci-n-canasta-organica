package com.example.huertohogardefinitiveedition.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    data class UiState(
        val username: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    )

    var uiState by mutableStateOf(UiState())
        private set

    fun onUsernameChange(newValue: String) {
        uiState = uiState.copy(username = newValue, error = null)
    }

    fun onPasswordChange(newValue: String) {
        uiState = uiState.copy(password = newValue, error = null)
    }

    // Reemplaza a "onError": así seteas/limpias el error desde la UI
    fun setError(message: String?) {
        uiState = uiState.copy(error = message)
    }

    fun setLoading(loading: Boolean) {
        uiState = uiState.copy(isLoading = loading)
    }
}