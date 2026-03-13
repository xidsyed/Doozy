package com.simple.doozy.feature.subscription.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.simple.doozy.feature.subscription.data.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val _isGeneratingOrder = MutableStateFlow(false)
    val isGeneratingOrder: StateFlow<Boolean> = _isGeneratingOrder.asStateFlow()

    fun createOrder(planId: String, onOrderCreated: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isGeneratingOrder.value = true
            subscriptionRepository.createOrderToBuySubscription(planId)
                .onSuccess { orderId ->
                    _isGeneratingOrder.value = false
                    onOrderCreated(orderId)
                }
                .onFailure {
                    _isGeneratingOrder.value = false
                    onError(it.toString())
                }
        }
    }
}
