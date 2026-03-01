package com.simple.doozy.feature.subscription.data

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SubscriptionPlan(val id: String, val name: String, val price: String)
data class Subscription(val plan: SubscriptionPlan, val status: String)

interface SubscriptionRepository {
    fun fetchUserSubscription(): Flow<Subscription?>
    suspend fun getAvailableSubscriptions(): Result<List<SubscriptionPlan>, Exception>
    suspend fun applyForSubscription(orderId: String): Result<Unit, Exception>
}

class DefaultSubscriptionRepository : SubscriptionRepository {
    private val mockPlan = SubscriptionPlan("pro_monthly", "Pro Monthly", "$9.99/mo")
    private val _subscriptionState = MutableStateFlow<Subscription?>(Subscription(mockPlan, "Active"))

    override fun fetchUserSubscription(): Flow<Subscription?> {
        return _subscriptionState.asStateFlow()
    }

    override suspend fun getAvailableSubscriptions(): Result<List<SubscriptionPlan>, Exception> {
        delay(1000)
        return Ok(listOf(mockPlan, SubscriptionPlan("pro_yearly", "Pro Yearly", "$99.99/yr")))
    }

    override suspend fun applyForSubscription(orderId: String): Result<Unit, Exception> {
        delay(1000)
        _subscriptionState.value = Subscription(mockPlan, "Active")
        return Ok(Unit)
    }
}
