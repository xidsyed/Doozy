package com.simple.doozy.feature.session

import com.simple.doozy.common.ui.util.SnackbarController
import com.simple.doozy.feature.auth.AuthState
import com.simple.doozy.feature.auth.data.AuthRepository
import com.simple.doozy.feature.subscription.data.SubscriptionRepository
import com.simple.doozy.feature.user.data.UserNotFoundException
import com.simple.doozy.feature.user.data.UserRepository
import com.simple.doozy.feature.user.data.UserState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SessionManager(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val snackbarController: SnackbarController,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {

    init {
        scope.launch {
            authRepository.state.collect { authState ->
                if (authState is AuthState.Authenticated) {
                    try {
                        userRepository.fetchUser(authState.userId)
                    } catch (e: UserNotFoundException) {
                        try {
                            userRepository.createNewUser(authState.userId)
                        } catch (createError: Exception) {
                            handleRegistrationError(createError)
                        }
                    } catch (e: Exception) {
                        handleRegistrationError(e)
                    }
                }
            }
        }
    }

    private suspend fun handleRegistrationError(e: Exception) {
        e.printStackTrace()
        snackbarController.showMessage("User registration failed. Please try again.")
        authRepository.logout()
    }

    val sessionState: StateFlow<SessionState> = combine(
        authRepository.state,
        userRepository.state,
        subscriptionRepository.fetchUserSubscription()
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
