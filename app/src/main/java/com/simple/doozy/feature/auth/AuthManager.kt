package com.simple.doozy.feature.auth

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.simple.doozy.feature.auth.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.uuid.ExperimentalUuidApi

class AuthManager {
    private val _state: MutableStateFlow<AuthState> = MutableStateFlow(AuthState.Checking)
    val state: Flow<AuthState> = _state.asStateFlow()

    private val _resendCountdown: MutableStateFlow<Int> = MutableStateFlow(0)
    val resendCountdown: Flow<Int> = _resendCountdown.asStateFlow()

    private var countdownJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    suspend fun initialize() {
        withContext(Dispatchers.IO) {
            delay(1_000)
            _state.update { AuthState.Unauthenticated }
        }
    }

    suspend fun sendOtp(phoneNumber: String): Result<Unit, Exception> {
        delay(1000)
        startCountdown()
        return Ok(Unit)
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun loginWithOtp(otp: String): Result<Unit, Exception> {
        delay(1000)
        return if (otp == "000000") { // We will accept 000000 as valid OTP for test
            _state.update { AuthState.Authenticated(User.MOCK.id) }
            Ok(Unit)
        } else {
            Err(Exception("Invalid OTP Code"))
        }
    }

    suspend fun resendOtp(): Result<Unit, Exception> {
        delay(1000)
        startCountdown()
        return Ok(Unit)
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        _resendCountdown.value = 30
        countdownJob = scope.launch {
            while (_resendCountdown.value > 0) {
                delay(1000)
                _resendCountdown.update { it - 1 }
            }
        }
    }

    suspend fun logout() {
        delay(1000)
        _state.update { AuthState.Unauthenticated }
    }

    suspend fun updateUser(user: User) {
        delay(1000)
        _state.update { AuthState.Registered(user) }
    }

}