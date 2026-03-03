package com.simple.doozy.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simple.doozy.feature.user.data.UserRepository
import com.simple.doozy.feature.user.data.UserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FaqItem(val question: String, val answer: String)

data class SupportState(
    val userId: String = "unknown",
    val expandedItemIndex: Int? = null,
    val faqs: List<FaqItem> = listOf(
        FaqItem(
            "How do I create a new task?",
            "Tap the \"+\" button at the bottom center of your dashboard. Enter your task title and press enter."
        ),
        FaqItem(
            "Can I sync with my calendar?",
            "Yes. Go to Settings > Integrations and select your preferred calendar provider to enable two-way sync."
        ),
        FaqItem(
            "How to set recurring reminders?",
            "Open any task, tap 'Set Due Date', and select 'Repeat'. You can choose daily, weekly, or custom intervals."
        ),
        FaqItem(
            "Where are my archived tasks?",
            "Archived tasks are stored in the 'Archive' tab found in the main navigation menu."
        ),
        FaqItem(
            "Is there a dark mode?",
            "Dark mode follows your system settings by default, or you can toggle it manually in Settings > Appearance."
        )
    )
)

sealed interface SupportAction {
    data class FaqClicked(val index: Int) : SupportAction
    data object NavigateBackClicked : SupportAction
    data object EmailSupportClicked : SupportAction
}

class SupportViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SupportState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.state.collectLatest { userState ->
                if (userState is UserState.Registered) {
                    _uiState.update { it.copy(userId = userState.user.id.id) }
                }
            }
        }
    }

    fun handleAction(action: SupportAction) {
        when (action) {
            is SupportAction.FaqClicked -> {
                _uiState.update {
                    it.copy(expandedItemIndex = if (it.expandedItemIndex == action.index) null else action.index)
                }
            }

            SupportAction.NavigateBackClicked -> {} // Handled via callback in UI
            SupportAction.EmailSupportClicked -> {} // Handled via Intent in UI
        }
    }
}
