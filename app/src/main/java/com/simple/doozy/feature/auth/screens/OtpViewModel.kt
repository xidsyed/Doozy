package com.simple.doozy.feature.auth.screens

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.simple.doozy.feature.auth.data.AuthError
import com.simple.doozy.feature.auth.data.AuthRepository
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

class OtpViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(OtpState())
    val state = _state.asStateFlow()
    private var phoneNumber: String = ""

    init {
        viewModelScope.launch {
            authRepository.resendCountdown.collect { countdown ->
                _state.update { it.copy(resendCountdown = countdown) }
            }
        }
    }

    fun initPhoneNumber(phone: String, activity: Activity) {
        if (phoneNumber.isEmpty()) {
            phoneNumber = phone
            sendOtp(activity)
        }
    }

    fun updateOtp(code: String) {
        if (code.length <= 6 && code.all { it.isDigit() }) {
            _state.update { it.copy(otpCode = code, error = null) }
        }
    }

    private fun sendOtp(activity: Activity) {
        viewModelScope.launch {
            _state.update { it.copy(isSendingCode = true, error = null) }
            authRepository.sendOtp("+91$phoneNumber", activity)
                .onSuccess {
                    _state.update { it.copy(isSendingCode = false) }
                }
                .onFailure { err ->
                    Log.d("TAG", err.message)
                    _state.update { it.copy(isSendingCode = false, error = mapErrorToString(err)) }
                }
        }
    }

    fun resendOtp(activity: Activity) {
        if (_state.value.resendCountdown == 0) {
            sendOtp(activity)
        }
    }

    fun verifyOtp() {
        if (_state.value.otpCode.length == 6) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, error = null) }
                authRepository.loginWithOtp(_state.value.otpCode)
                    .onSuccess {
                        _state.update { it.copy(isLoading = false, error = null) }
                    }
                    .onFailure { err ->
                        _state.update { it.copy(isLoading = false, error = mapErrorToString(err)) }
                    }
            }
        }
    }

    private fun mapErrorToString(error: AuthError): String {
        return when (error) {
            is AuthError.InvalidOtp -> "Incorrect code entered. Please try again."
            is AuthError.InvalidPhoneNumber -> "The format of the phone number provided is incorrect."
            is AuthError.TooManyRequests -> "Too many attempts. Please try again later."
            is AuthError.Timeout -> "Verification timed out. Please request a new code."
            is AuthError.NetworkError -> "No internet connection. Check your network."
            is AuthError.CodeSentFailed -> "Failed to send code. Please try again."
            is AuthError.Unknown -> if (error.message.isNotBlank()) error.message else "An unexpected error occurred. Please try again."
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TAG", "onCleared")
    }
}
