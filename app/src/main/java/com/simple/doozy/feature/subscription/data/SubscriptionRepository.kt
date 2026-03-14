package com.simple.doozy.feature.subscription.data

import androidx.datastore.core.DataStore
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface SubscriptionRepository {
    val state: StateFlow<SubscriptionRepositoryState>
    suspend fun getAvailableSubscriptions(): Result<List<SubscriptionPlan>, AppError>
    suspend fun createOrderToBuySubscription(planId: String): Result<String, AppError> // Returns the orderId
    suspend fun activateSubscription(): Result<Unit, AppError>
    suspend fun cancelActiveSubscription(): Result<Unit, AppError>
}

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultSubscriptionRepository(
    private val authRepository: AuthRepository,
    private val subscriptionDataStore: DataStore<SubscriptionData>
) : SubscriptionRepository {
    private val mockPlan = SubscriptionData.Active.MOCK.plan
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _state = MutableStateFlow(SubscriptionRepositoryState(SyncStatus.Idle(System.currentTimeMillis()), null))
    override val state: StateFlow<SubscriptionRepositoryState> = _state.asStateFlow()

    // Simulate real-time backend stream
    private val remoteState: Flow<Result<SubscriptionData, AppError>> = authRepository.state.flatMapLatest { authState ->
        when (authState) {
            is AuthState.Authenticated -> {
                flow {
                    // Mock remote fetch
                    delay(500)
                    val currentData = subscriptionDataStore.data.first()
                    // Re-emit existing plan if there is one, keeping existing data mock backend keeping state
                    if (currentData is SubscriptionData.Active) {
                        emit(Ok(currentData))
                    } else {
                        emit(Ok(SubscriptionData.NoSubscription(System.currentTimeMillis())))
                    }
                }
            }
            else -> emptyFlow()
        }
    }

    init {
        // Collect from the DataStore and instantly update our local state `data` property.
        scope.launch {
            subscriptionDataStore.data.collect { storedData ->
                _state.update { it.copy(data = storedData) }
            }
        }

        // Collect from the remote stream and persist it to DataStore.
        scope.launch {
            remoteState.collect { result ->
                result.onSuccess { data: SubscriptionData ->
                    subscriptionDataStore.updateData { data }
                    _state.update { it.copy(syncStatus = SyncStatus.Idle(data.lastSyncTimestamp)) }
                }.onFailure { error ->
                    _state.update { it.copy(syncStatus = SyncStatus.Error("Failed to sync remote state: $error")) }
                }
            }
        }
    }

    override suspend fun getAvailableSubscriptions(): Result<List<SubscriptionPlan>, AppError> {
        delay(1000)
        return Ok(listOf(mockPlan, SubscriptionPlan("pro_yearly", "Pro Yearly", 9999)))
    }

    override suspend fun createOrderToBuySubscription(planId: String): Result<String, AppError> {
        delay(1000)
        return Ok("demo_order_id")
    }

    override suspend fun cancelActiveSubscription(): Result<Unit, AppError> {
        return try {
            _state.update { it.copy(syncStatus = SyncStatus.Loading) }
            // Mock network call to backend
            delay(1000)
            
            // Backend successfully cancelled, update local datastore immediately
            val timestamp = java.lang.System.currentTimeMillis()
            subscriptionDataStore.updateData { SubscriptionData.NoSubscription(timestamp) }
            _state.update { it.copy(syncStatus = SyncStatus.Idle(timestamp)) }
            Ok(Unit)
        } catch (e: Exception) {
            _state.update { it.copy(syncStatus = SyncStatus.Error("Network error")) }
            Err(AppError.Network)
        }
    }

    override suspend fun activateSubscription(): Result<Unit, AppError> {
        return try {
            _state.update { it.copy(syncStatus = SyncStatus.Loading) }
            // Mock network request to backend
            delay(500)
            
            // Backend successfully activated, update local datastore immediately
            val timestamp = System.currentTimeMillis()
            subscriptionDataStore.updateData { 
                SubscriptionData.Active.MOCK.copy(lastSyncTimestamp = timestamp)
            }
            _state.update { it.copy(syncStatus = SyncStatus.Idle(timestamp)) }
            Ok(Unit)
        } catch (e: Exception) {
            _state.update { it.copy(syncStatus = SyncStatus.Error("Network error: ${e.message}")) }
            Err(AppError.Network)
        }
    }
}
