package com.simple.doozy.feature.auth.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simple.doozy.feature.auth.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null
)

class LoginViewModel(private val authManager: AuthManager) : ViewModel() {

    private val _state = MutableStateFlow(
        LoginState(
            isLoading = false,
            error = null
        )
    )
    val state = _state.asStateFlow()

    fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            if (!authManager.login()) {
                _state.update { it.copy(isLoading = false, error = "Login failed") }
            } else {
                _state.update {
                    it.copy(isLoading = false, error = null)
                }
            }
        }
    }

}