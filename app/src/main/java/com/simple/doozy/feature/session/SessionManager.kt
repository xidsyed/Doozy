package com.simple.doozy.feature.session

import com.simple.doozy.feature.auth.AuthState
import com.simple.doozy.feature.auth.data.AuthRepository
import com.simple.doozy.feature.subscription.data.SubscriptionRepository
import com.simple.doozy.feature.user.data.UserRepository
import com.simple.doozy.feature.user.data.UserState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class SessionManager(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {

    val sessionState: StateFlow<SessionState> = combine(
        authRepository.state,
        userRepository.state,
        subscriptionRepository.state
    ) { authState, userState, subscriptionState ->
        when (authState) {
            is AuthState.Checking -> SessionState.Checking
            is AuthState.Unauthenticated -> SessionState.Unauthenticated
            is AuthState.Authenticated -> {
                when (userState) {
                    is UserState.Registered -> SessionState.Registered(userState.user, subscriptionState)
                    else -> SessionState.Authenticated(authState.userId) // Includes Checking, NotFound states during the transition
                }
            }
        }
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SessionState.Checking
    )
}
