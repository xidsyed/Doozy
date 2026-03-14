package com.simple.doozy.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simple.doozy.feature.auth.model.User
import com.simple.doozy.feature.user.data.UserRepository
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class EditProfileState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val nameInput: String = "",
    val genderInput: String = "Prefer not to say",
    val emailInput: String = "",
    val subscribeInput: Boolean = false
)

class EditProfileViewModel(
    private val userRepository: UserRepository,
    private val snackbarController: com.simple.doozy.common.ui.util.SnackbarController
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileState())
    val uiState = _uiState.asStateFlow()

    private val eventChannel = Channel<EditProfileEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            userRepository.state.collectLatest { userState ->
                val user = userState.data

                _uiState.update {
                    it.copy(
                        user = user,
                        isLoading = false,
                        nameInput = user?.name ?: "",
                        genderInput = user?.gender ?: "Prefer not to say",
                        emailInput = user?.email ?: "",
                        subscribeInput = user?.subscribeToEmails ?: false
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

        if (currentState.nameInput.isBlank()) {
            snackbarController.showMessage("Name cannot be empty")
            return
        }

        if (currentState.emailInput.isBlank()) {
            snackbarController.showMessage("Email cannot be empty")
            return
        }

        if (currentState.genderInput == "Prefer not to say" || currentState.genderInput.isBlank()) {
            snackbarController.showMessage("Please select a gender")
            return
        }

        val updatedUser = currentUser.copy(
            name = currentState.nameInput,
            email = currentState.emailInput,
            gender = currentState.genderInput,
            subscribeToEmails = currentState.subscribeInput
        )

        viewModelScope.launch {
            withContext(NonCancellable) {
                userRepository.updateUser(updatedUser)
                snackbarController.showMessage("Profile updated successfully")
                eventChannel.send(EditProfileEvent.NavigateBack)
            }
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

sealed interface EditProfileEvent {
    data object NavigateBack : EditProfileEvent
}
