package com.simple.doozy.feature.subscription.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.simple.doozy.core.error.AppError
import com.simple.doozy.feature.auth.AuthState
import com.simple.doozy.feature.auth.data.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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
    suspend fun getAvailableSubscriptions(): Result<List<SubscriptionPlan>, AppError>
    suspend fun createOrderToBuySubscription(planId: String): Result<String, AppError> // Returns the orderId
    suspend fun activateSubscription(): Result<Unit, AppError>
    suspend fun cancelActiveSubscription(): Result<Unit, AppError>
}

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultSubscriptionRepository(
    private val authRepository: AuthRepository,
    private val dataStore: DataStore<Preferences>
) : SubscriptionRepository {
    private val mockPlan = SubscriptionPlan("pro_monthly", "Pro Monthly", "$9.99/mo")
    private val scope = CoroutineScope(Dispatchers.IO)

    // Simulate real-time backend stream (e.g. from RevenueCat webhooks updating ConvexDB)
    private val remoteState: Flow<Result<SubscriptionState, AppError>> = authRepository.state.flatMapLatest { authState ->
        when (authState) {
            is AuthState.Authenticated -> {
                flow {
                    // Mock remote fetch
                    delay(500)
                    
                    val prefs = dataStore.data.first()
                    val existingPlanId = prefs[KEY_SUBSCRIPTION_PLAN_ID]
                    
                    if (existingPlanId != null) {
                        emit(Ok(SubscriptionState.Subscribed(
                            plan = if (existingPlanId == mockPlan.id) mockPlan else SubscriptionPlan(
                                existingPlanId,
                                "Unknown Plan",
                                ""
                            ),
                            subscribedOn = "Cached",
                            billingDate = "Cached",
                            expiresOn = "Cached"
                        )))
                    } else {
                        emit(Ok(SubscriptionState.NoSubscription))
                    }
                }
            }
            else -> emptyFlow()
        }
    }

    companion object {
        val KEY_SUBSCRIPTION_PLAN_ID = stringPreferencesKey("subscription_plan_id")
    }

    init {
        // Collect from the remote stream and persist it to DataStore.
        scope.launch {
            remoteState.collect { result ->
                result.onSuccess { state ->
                    when (state) {
                        is SubscriptionState.Subscribed -> {
                            dataStore.edit { prefs ->
                                prefs[KEY_SUBSCRIPTION_PLAN_ID] = state.plan.id
                            }
                        }
                        is SubscriptionState.NoSubscription -> {
                            dataStore.edit { prefs ->
                                prefs.remove(KEY_SUBSCRIPTION_PLAN_ID)
                            }
                        }
                        else -> {}
                    }
                }.onFailure { error ->
                    when (error) {
                        is AppError.Network -> {
                            // Ignore network errors, let local-first cache serve the UI
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    override fun fetchUserSubscription(): Flow<SubscriptionState> {
        // Instantly emit whatever is in the local DataStore cache.
        return dataStore.data.map { prefs ->
            val planId = prefs[KEY_SUBSCRIPTION_PLAN_ID]
            if (planId != null) {
                // For demonstration, map the string back to the mock plan
                SubscriptionState.Subscribed(
                    plan = if (planId == mockPlan.id) mockPlan else SubscriptionPlan(
                        planId,
                        "Unknown Plan",
                        ""
                    ),
                    subscribedOn = "Cached",
                    billingDate = "Cached",
                    expiresOn = "Cached"
                )
            } else {
                SubscriptionState.NoSubscription
            }
        }
    }

    override suspend fun getAvailableSubscriptions(): Result<List<SubscriptionPlan>, AppError> {
        delay(1000)
        return Ok(listOf(mockPlan, SubscriptionPlan("pro_yearly", "Pro Yearly", "$99.99/yr")))
    }

    override suspend fun createOrderToBuySubscription(planId: String): Result<String, AppError> {
        delay(1000)
        return Ok("demo_order_id")
    }

    override suspend fun cancelActiveSubscription(): Result<Unit, AppError> {
        return try {
            // Mock network request Call to backend
            delay(1000)
            
            // Backend successfully cancelled, update local datastore immediately
            dataStore.edit { prefs ->
                prefs.remove(KEY_SUBSCRIPTION_PLAN_ID)
            }
            Ok(Unit)
        } catch (e: Exception) {
            Err(AppError.Network)
        }
    }

    override suspend fun activateSubscription(): Result<Unit, AppError> {
        return try {
            // Mock network request to backend
            delay(500)
            
            // Backend successfully activated, update local datastore immediately
            dataStore.edit { prefs ->
                prefs[KEY_SUBSCRIPTION_PLAN_ID] = mockPlan.id
            }
            Ok(Unit)
        } catch (e: Exception) {
            Err(AppError.Network)
        }
    }
}
