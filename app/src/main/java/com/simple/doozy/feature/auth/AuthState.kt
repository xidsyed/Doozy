package com.simple.doozy.feature.auth

sealed interface AuthState {
    data object Checking : AuthState
    data object Unauthenticated : AuthState
    data class Authenticated(val userId: String, val authToken: String) : AuthState
}