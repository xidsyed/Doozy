package com.simple.doozy.feature.subscription.data

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SubscriptionPlan(val id: String, val name: String, val price: String)

sealed interface SubscriptionState {
    data object Checking : SubscriptionState
    data object NoSubscription : SubscriptionState
    data class Subscribed(
        val plan: SubscriptionPlan,
        val subscribedOn: String,
        val billingDate: String,
        val expiresOn: String
    ) : SubscriptionState
}

interface SubscriptionRepository {
    fun fetchUserSubscription(): Flow<SubscriptionState>
    suspend fun getAvailableSubscriptions(): Result<List<SubscriptionPlan>, Exception>
    suspend fun createOrderToBuySubscription(planId: String): Result<String, Exception> // Return sthe orderId
    suspend fun cancelActiveSubscription(): Result<Unit, Exception>
}

class DefaultSubscriptionRepository : SubscriptionRepository {
    private val mockPlan = SubscriptionPlan("pro_monthly", "Pro Monthly", "$9.99/mo")
    private val _subscriptionState = MutableStateFlow<SubscriptionState>(SubscriptionState.Checking)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            delay(1500)
            _subscriptionState.value = SubscriptionState.NoSubscription
        }
    }

    override fun fetchUserSubscription(): Flow<SubscriptionState> {
        return _subscriptionState.asStateFlow()
    }

    override suspend fun getAvailableSubscriptions(): Result<List<SubscriptionPlan>, Exception> {
        delay(1000)
        return Ok(listOf(mockPlan, SubscriptionPlan("pro_yearly", "Pro Yearly", "$99.99/yr")))
    }

    override suspend fun createOrderToBuySubscription(planId: String): Result<String, Exception> {
        delay(1000)
        return Ok("demo_order_id")
    }

    override suspend fun cancelActiveSubscription(): Result<Unit, Exception> {
        delay(1000)
        _subscriptionState.value = SubscriptionState.NoSubscription
        return Ok(Unit)
    }
}

