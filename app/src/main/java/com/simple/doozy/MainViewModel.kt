package com.simple.doozy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simple.doozy.feature.auth.AuthManager
import com.simple.doozy.feature.auth.AuthState
import com.simple.doozy.feature.onboarding.OnboardingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val authManager: AuthManager,
    private val onboardingRepo: OnboardingRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            authManager.initialize()
        }
    }

    val authState = authManager.state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(3000),
        AuthState.Checking
    )
    val onboardingCompleted = onboardingRepo.hasCompletedOnboarding.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(3000),
        false
    )

    init {
        viewModelScope.launch {
//            authManager.login()
        }
    }
}