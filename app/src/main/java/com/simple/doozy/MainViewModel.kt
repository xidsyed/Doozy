package com.simple.doozy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simple.doozy.feature.auth.data.AuthRepository
import com.simple.doozy.feature.session.SessionManager
import com.simple.doozy.feature.session.SessionState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    init {
        viewModelScope.launch {
            // Initialization is now handled by the callbackFlow when state is collected
        }
    }

    val sessionState = sessionManager.sessionState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(3000),
        SessionState.Checking
    )


    init {
        viewModelScope.launch {
//            authManager.login()
        }
    }
}