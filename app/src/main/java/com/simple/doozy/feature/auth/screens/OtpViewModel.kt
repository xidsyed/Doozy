package com.simple.doozy.feature.auth.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simple.doozy.feature.auth.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OtpState(
    val otpCode: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class OtpViewModel(private val authManager: AuthManager) : ViewModel() {

    private val _state = MutableStateFlow(OtpState())
    val state = _state.asStateFlow()

    fun updateOtp(code: String) {
        if (code.length <= 6 && code.all { it.isDigit() }) {
            _state.update { it.copy(otpCode = code, error = null) }
        }
    }

    fun verifyOtp() {
        if (_state.value.otpCode.length == 6) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, error = null) }
                if (!authManager.login()) {
                    _state.update { it.copy(isLoading = false, error = "Verification failed") }
                } else {
                    _state.update { it.copy(isLoading = false, error = null) }
                    // App navigation handles auth state change automatically 
                }
            }
        }
    }
}
