package com.simple.doozy.feature.auth

import com.simple.doozy.feature.auth.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlin.uuid.ExperimentalUuidApi

// TODO : Manage errors with https://github.com/michaelbull/kotlin-result

class AuthManager {
    private val _state: MutableStateFlow<AuthState> = MutableStateFlow(AuthState.Checking)
    val state: Flow<AuthState> = _state.asStateFlow()

    suspend fun initialize() {
        withContext(Dispatchers.IO) {
            delay(1_000)
            _state.update { AuthState.Unauthenticated }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun login(forceFailure: Boolean = false): Boolean {
        delay(1000)
        _state.update {
            if (forceFailure) AuthState.Unauthenticated else AuthState.Authenticated(
                User.MOCK.id
            )
        }
        return !forceFailure
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