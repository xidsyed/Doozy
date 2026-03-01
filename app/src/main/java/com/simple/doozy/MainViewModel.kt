package com.simple.doozy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simple.doozy.feature.auth.AuthManager
import com.simple.doozy.feature.auth.AuthState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val authManager: AuthManager,
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


    init {
        viewModelScope.launch {
//            authManager.login()
        }
    }
}