package com.simple.doozy.feature.auth.data

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.simple.doozy.feature.auth.AuthState
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

interface AuthRepository {
    val state: Flow<AuthState>
    val resendCountdown: Flow<Int>

    suspend fun initialize()
    suspend fun sendOtp(phoneNumber: String): Result<Unit, Exception>
    suspend fun loginWithOtp(otp: String): Result<Unit, Exception>
    suspend fun resendOtp(): Result<Unit, Exception>
    suspend fun logout()
    fun getCurrentToken(): String?
}

class DefaultAuthRepository : AuthRepository {
    private val _state: MutableStateFlow<AuthState> = MutableStateFlow(AuthState.Checking)
    override val state: Flow<AuthState> = _state.asStateFlow()

    private val _resendCountdown: MutableStateFlow<Int> = MutableStateFlow(0)
    override val resendCountdown: Flow<Int> = _resendCountdown.asStateFlow()

    private var countdownJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    override suspend fun initialize() {
        withContext(Dispatchers.IO) {
            delay(1_000)
            _state.update { AuthState.Unauthenticated }
        }
    }

    override suspend fun sendOtp(phoneNumber: String): Result<Unit, Exception> {
        delay(1000)
        startCountdown()
        return Ok(Unit)
    }

    override suspend fun loginWithOtp(otp: String): Result<Unit, Exception> {
        delay(1000)
        return if (otp == "000000") { // We will accept 000000 as valid OTP for test
            _state.update { AuthState.Authenticated(User.MOCK.id, "mock_token") }
            Ok(Unit)
        } else {
            Err(Exception("Invalid OTP Code"))
        }
    }

    override suspend fun resendOtp(): Result<Unit, Exception> {
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

    override suspend fun logout() {
        delay(1000)
        _state.update { AuthState.Unauthenticated }
    }

    override fun getCurrentToken(): String? {
        val currentState = _state.value
        return if (currentState is AuthState.Authenticated) {
            currentState.authToken
        } else {
            null
        }
    }
}
