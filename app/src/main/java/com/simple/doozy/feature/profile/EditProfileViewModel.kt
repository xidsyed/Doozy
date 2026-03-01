package com.simple.doozy.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simple.doozy.feature.auth.AuthManager
import com.simple.doozy.feature.auth.AuthState
import com.simple.doozy.feature.auth.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditProfileState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val nameInput: String = "",
    val genderInput: String = "Prefer not to say",
    val emailInput: String = "",
    val subscribeInput: Boolean = false
)

class EditProfileViewModel(
    private val authManager: AuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileState())
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

                _uiState.update {
                    it.copy(
                        user = user,
                        isLoading = false,
                        nameInput = user?.id?.name ?: "",
                        genderInput = user?.metadata?.gender ?: "Prefer not to say",
                        emailInput = user?.id?.email ?: "",
                        subscribeInput = user?.metadata?.subscribeToEmails ?: false
                    )
                }
            }
        }
    }

    fun handleAction(action: EditProfileAction) {
        when (action) {
            is EditProfileAction.NameChanged -> _uiState.update { it.copy(nameInput = action.name) }
            is EditProfileAction.GenderChanged -> _uiState.update { it.copy(genderInput = action.gender) }
            is EditProfileAction.EmailChanged -> _uiState.update { it.copy(emailInput = action.email) }
            is EditProfileAction.SubscribeChanged -> _uiState.update { it.copy(subscribeInput = action.subscribed) }
            is EditProfileAction.SaveClicked -> saveProfile()
        }
    }

    private fun saveProfile() {
        val currentState = _uiState.value
        val currentUser = currentState.user ?: return

        val updatedUser = currentUser.copy(
            id = currentUser.id.copy(
                name = currentState.nameInput,
                email = currentState.emailInput
            ),
            metadata = currentUser.metadata.copy(
                gender = currentState.genderInput,
                subscribeToEmails = currentState.subscribeInput
            )
        )

        viewModelScope.launch {
            authManager.updateUser(updatedUser)
        }
    }
}

sealed interface EditProfileAction {
    data class NameChanged(val name: String) : EditProfileAction
    data class GenderChanged(val gender: String) : EditProfileAction
    data class EmailChanged(val email: String) : EditProfileAction
    data class SubscribeChanged(val subscribed: Boolean) : EditProfileAction
    data object SaveClicked : EditProfileAction
}
