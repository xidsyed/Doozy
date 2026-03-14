package com.simple.doozy.feature.user.data

import androidx.datastore.core.DataStore
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.simple.doozy.core.data.SyncStatus
import com.simple.doozy.core.error.AppError
import com.simple.doozy.feature.auth.AuthState
import com.simple.doozy.feature.auth.data.AuthRepository
import com.simple.doozy.feature.auth.model.User
import com.simple.doozy.feature.session.UserSessionClearable
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

interface UserRepository : UserSessionClearable {
    val state: StateFlow<UserRepositoryState>
    suspend fun createUser(userId: String): Result<Unit, AppError>
    suspend fun updateUser(user: User): Result<Unit, AppError>
}

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultUserRepository(
    private val authRepository: AuthRepository,
    private val dataStore: DataStore<User?>
) : UserRepository {
    
    private val scope = CoroutineScope(Dispatchers.IO)
    
    private val _state = MutableStateFlow(UserRepositoryState(SyncStatus.Idle(System.currentTimeMillis()), null))
    override val state: StateFlow<UserRepositoryState> = _state.asStateFlow()
    
    // Simulate real-time backend stream (e.g. ConvexDB)
    private val remoteState: Flow<Result<User, AppError>> = authRepository.state.flatMapLatest { authState ->
        when (authState) {
            is AuthState.Authenticated -> {
                flow {
                    // Mock remote fetch (this would be your Convex query)
                    delay(500)
                    
                    val existingUser = dataStore.data.first()
                    
                    // Simulate fetching a user that exists remotely
                    if (existingUser != null && existingUser.id == authState.userId) {
                        emit(Ok(existingUser))
                    } else if (existingUser == null) {
                        // Create a mock user if they don't exist in local cache but are authenticated
                        val mockRemoteUser = User(id = authState.userId)
                        emit(Ok(mockRemoteUser))
                    } else {
                        // Simulate User Not Found remotely if IDs don't match
                        emit(Err(AppError.NotFound))
                    }
                }
            }
            is AuthState.Unauthenticated -> {
               // Handle logout cleanup
               dataStore.updateData { null }
               emptyFlow()
            }
            else -> emptyFlow()
        }
    }

    init {
        // Collect from the DataStore and instantly update our local state `data` property.
        scope.launch {
            dataStore.data.collect { storedData ->
                _state.update { it.copy(data = storedData) }
            }
        }

        // Collect from the reactive remote stream and persist it to DataStore.
        scope.launch {
            remoteState.collect { result ->
                result.onSuccess { user ->
                    dataStore.updateData { user }
                    _state.update { it.copy(syncStatus = SyncStatus.Idle(System.currentTimeMillis())) }
                }.onFailure { error ->
                    when (error) {
                        is AppError.NotFound -> {
                            // In reality, if user was deleted remotely, we'd clear the cache
                            dataStore.updateData { null }
                            _state.update { it.copy(syncStatus = SyncStatus.Idle(System.currentTimeMillis())) }
                        }
                        is AppError.Network -> {
                            // Ignore network errors, let local-first cache serve the UI
                            _state.update { it.copy(syncStatus = SyncStatus.Error("Network Error")) }
                        }
                        is AppError.Unknown -> {
                            _state.update { it.copy(syncStatus = SyncStatus.Error("Unknown Error")) }
                        }
                    }
                }
            }
        }
    }

    override suspend fun createUser(userId: String): Result<Unit, AppError> {
        val token = authRepository.getCurrentToken()
        if (token == null) {
            return Err(AppError.Unknown(IllegalStateException("No auth token available")))
        }
        
        return try {
            _state.update { it.copy(syncStatus = SyncStatus.Loading) }
            // Mock remote creation (Convex mutation)
            delay(500)
            
            // Force save to datastore so our local state knows we are registered now
            val newUser = User(id = userId)
            val timestamp = System.currentTimeMillis()
            dataStore.updateData { newUser }
            _state.update { it.copy(syncStatus = SyncStatus.Idle(timestamp)) }
            Ok(Unit)
        } catch (e: Exception) {
            _state.update { it.copy(syncStatus = SyncStatus.Error("Network Error")) }
            Err(AppError.Network)
        }
    }

    override suspend fun updateUser(user: User): Result<Unit, AppError> {
        return try {
            _state.update { it.copy(syncStatus = SyncStatus.Loading) }
            // Mock remote update (Convex mutation)
            delay(500)
            
            // Force save to datastore 
            val timestamp = System.currentTimeMillis()
            dataStore.updateData { user }
            _state.update { it.copy(syncStatus = SyncStatus.Idle(timestamp)) }
            Ok(Unit)
        } catch (e: Exception) {
             _state.update { it.copy(syncStatus = SyncStatus.Error("Network Error")) }
            Err(AppError.Network)
        }
    }

    override suspend fun clearSessionData() {
        dataStore.updateData { null }
        _state.update { it.copy(syncStatus = SyncStatus.Idle(System.currentTimeMillis())) }
    }
}
