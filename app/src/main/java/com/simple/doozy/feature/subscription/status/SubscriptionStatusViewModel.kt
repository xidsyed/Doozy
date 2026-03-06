package com.simple.doozy.feature.subscription.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simple.doozy.feature.subscription.data.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SubscriptionStatusViewModel(
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val _isCancelling = MutableStateFlow(false)
    val isCancelling: StateFlow<Boolean> = _isCancelling.asStateFlow()

    fun cancelSubscription(onCancelled: () -> Unit) {
        viewModelScope.launch {
            _isCancelling.value = true
            subscriptionRepository.cancelActiveSubscription()
            _isCancelling.value = false
            onCancelled()
        }
    }
}
