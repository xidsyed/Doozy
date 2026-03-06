package com.simple.doozy.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AccountPrivacyState(
    val isDeleting: Boolean = false
)

sealed interface AccountPrivacyAction {
    data object DeleteAccountClicked : AccountPrivacyAction
    data object NavigateBackClicked : AccountPrivacyAction
}

sealed interface AccountPrivacyEvent {
    data object NavigateBack : AccountPrivacyEvent
    data object AccountDeleted : AccountPrivacyEvent
}

class AccountPrivacyViewModel(
    private val deleteAccountUseCase: DeleteAccountUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(AccountPrivacyState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<AccountPrivacyEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun handleAction(action: AccountPrivacyAction) {
        when (action) {
            AccountPrivacyAction.DeleteAccountClicked -> deleteAccount()
            AccountPrivacyAction.NavigateBackClicked -> viewModelScope.launch {
                _uiEvent.emit(AccountPrivacyEvent.NavigateBack)
            }
        }
    }

    private fun deleteAccount() {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }
            try {
                deleteAccountUseCase()
                _uiEvent.emit(AccountPrivacyEvent.AccountDeleted)
            } finally {
                _uiState.update { it.copy(isDeleting = false) }
            }
        }
    }
}
