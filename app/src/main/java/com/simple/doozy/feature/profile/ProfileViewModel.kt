package com.simple.doozy.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simple.doozy.core.data.SyncStatus
import com.simple.doozy.feature.auth.data.AuthRepository
import com.simple.doozy.feature.auth.model.User
import com.simple.doozy.feature.subscription.data.SubscriptionRepository
import com.simple.doozy.feature.subscription.data.SubscriptionRepositoryState
import com.simple.doozy.feature.user.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileState(
    val user: User? = null,
    val subscription: SubscriptionRepositoryState = SubscriptionRepositoryState(SyncStatus.Idle(0L), null),
    val isLoading: Boolean = true
)

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.state.collectLatest { userState ->
                val user = userState.data
                _uiState.update { it.copy(user = user, isLoading = false) }
            }
        }
        viewModelScope.launch {
            subscriptionRepository.state.collectLatest { sub ->
                _uiState.update { it.copy(subscription = sub) }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
