package com.simple.doozy.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simple.doozy.feature.auth.AuthManager
import com.simple.doozy.feature.auth.AuthState
import com.simple.doozy.feature.auth.model.User
import com.simple.doozy.feature.subscription.data.Subscription
import com.simple.doozy.feature.subscription.data.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileState(
    val user: User? = null,
    val subscription: Subscription? = null,
    val isLoading: Boolean = true
)

class ProfileViewModel(
    private val authManager: AuthManager,
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authManager.state.collectLatest { authState ->
                val user = when (authState) {
                    is AuthState.Authenticated -> {
                        if (authState.id.id == User.MOCK.id.id) User.MOCK else User(
                            authState.id,
                            User.Metadata(subscribeToEmails = true, gender = null)
                        )
                    }

                    is AuthState.Registered -> authState.user
                    else -> null
                }
                _uiState.update { it.copy(user = user, isLoading = false) }
            }
        }
        viewModelScope.launch {
            subscriptionRepository.fetchUserSubscription().collectLatest { sub ->
                _uiState.update { it.copy(subscription = sub) }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authManager.logout()
        }
    }
}
