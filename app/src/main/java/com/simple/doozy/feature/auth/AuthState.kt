package com.simple.doozy.feature.auth

import com.simple.doozy.feature.auth.model.User

sealed interface AuthState {
    data object Checking : AuthState
    data object Unauthenticated : AuthState
    data class Authenticated(val id: User.Id) : AuthState
    data class Registered(val user: User) : AuthState
}