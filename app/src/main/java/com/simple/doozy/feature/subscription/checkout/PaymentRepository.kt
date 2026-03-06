package com.simple.doozy.feature.subscription.checkout

import com.simple.doozy.feature.subscription.data.SubscriptionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex

sealed interface PaymentState {
    data object Idle : PaymentState
    data object Initiating : PaymentState
    data object Ongoing : PaymentState
    data object Success : PaymentState
    data class Failure(val message: String) : PaymentState
}

interface PaymentRepository {
    val paymentState: Flow<PaymentState>
    suspend fun initiate(orderId: String)
    fun resetPaymentState()
}

class DefaultPaymentRepository(
    private val subscriptionRepository: SubscriptionRepository
) : PaymentRepository {

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    override val paymentState: Flow<PaymentState> = _paymentState.asStateFlow()

    private val checkoutMutex = Mutex()

    override suspend fun initiate(orderId: String) {
        if (!checkoutMutex.tryLock()) {
            return // Checkout already in progress
        }

        try {
            _paymentState.value = PaymentState.Initiating
            delay(1000)

            _paymentState.value = PaymentState.Ongoing
            delay(2000) // Mocking payment processing

            // Simulate Successful payment
            _paymentState.value = PaymentState.Success

            // Wait a little before updating subscription repo to simulate real-world webhook delay
            delay(1000)
            subscriptionRepository.activateSubscription()

        } catch (e: Exception) {
            _paymentState.value = PaymentState.Failure(e.message ?: "Unknown error")
        } finally {
            checkoutMutex.unlock()
        }
    }

    override fun resetPaymentState() {
        _paymentState.value = PaymentState.Idle
    }
}
