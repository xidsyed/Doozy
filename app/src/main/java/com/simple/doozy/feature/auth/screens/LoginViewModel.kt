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
    val error: String? = null,
    val phoneNumber: String = ""
)

class LoginViewModel(private val authManager: AuthManager) : ViewModel() {

    private val _state = MutableStateFlow(
        LoginState(
            isLoading = false,
            error = null,
            phoneNumber = ""
        )
    )
    val state = _state.asStateFlow()

    fun updatePhoneNumber(phone: String) {
        _state.update { it.copy(phoneNumber = phone) }
    }

    // We can keep login() if we wanted to do some validation, but we can also just let UI navigate.
    fun validatePhone(): Boolean {
        // basic validation
        return _state.value.phoneNumber.length >= 10
    }
}