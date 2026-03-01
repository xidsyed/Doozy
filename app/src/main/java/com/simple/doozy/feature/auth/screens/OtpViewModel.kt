package com.simple.doozy.feature.auth.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.simple.doozy.feature.auth.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OtpState(
    val otpCode: String = "",
    val isLoading: Boolean = false,
    val isSendingCode: Boolean = false,
    val error: String? = null,
    val resendCountdown: Int = 0
)

class OtpViewModel(private val authManager: AuthManager) : ViewModel() {

    private val _state = MutableStateFlow(OtpState())
    val state = _state.asStateFlow()
    private var phoneNumber: String = ""

    init {
        viewModelScope.launch {
            authManager.resendCountdown.collect { countdown ->
                _state.update { it.copy(resendCountdown = countdown) }
            }
        }
    }

    fun initPhoneNumber(phone: String) {
        if (phoneNumber.isEmpty()) {
            phoneNumber = phone
            sendOtp()
        }
    }

    fun updateOtp(code: String) {
        if (code.length <= 6 && code.all { it.isDigit() }) {
            _state.update { it.copy(otpCode = code, error = null) }
        }
    }

    private fun sendOtp() {
        viewModelScope.launch {
            _state.update { it.copy(isSendingCode = true, error = null) }
            authManager.sendOtp(phoneNumber)
                .onSuccess {
                    _state.update { it.copy(isSendingCode = false) }
                }
                .onFailure { err ->
                    _state.update { it.copy(isSendingCode = false, error = err.message ?: "Failed to send code") }
                }
        }
    }

    fun resendOtp() {
        if (_state.value.resendCountdown == 0) {
            viewModelScope.launch {
                _state.update { it.copy(isSendingCode = true, error = null) }
                authManager.resendOtp()
                    .onSuccess {
                        _state.update { it.copy(isSendingCode = false) }
                    }
                    .onFailure { err ->
                        _state.update { it.copy(isSendingCode = false, error = err.message ?: "Failed to resend code") }
                    }
            }
        }
    }

    fun verifyOtp() {
        if (_state.value.otpCode.length == 6) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, error = null) }
                authManager.loginWithOtp(_state.value.otpCode)
                    .onSuccess {
                        _state.update { it.copy(isLoading = false, error = null) }
                    }
                    .onFailure { err ->
                        _state.update { it.copy(isLoading = false, error = err.message ?: "Verification failed") }
                    }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TAG", "onCleared")
    }
}
