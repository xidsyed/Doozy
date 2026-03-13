package com.simple.doozy.feature.user.data

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
import com.simple.doozy.feature.auth.model.User
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

interface UserRepository {
    val state: Flow<UserState>
    suspend fun createNewUser(userId: String): Result<Unit, AppError>
    suspend fun updateUser(user: User): Result<Unit, AppError>
}

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultUserRepository(
    private val authRepository: AuthRepository,
    private val dataStore: DataStore<Preferences>
) : UserRepository {
    
    private val scope = CoroutineScope(Dispatchers.IO)
    
    // Simulate real-time backend stream (e.g. ConvexDB)
    private val remoteState: Flow<Result<UserState, AppError>> = authRepository.state.flatMapLatest { authState ->
        when (authState) {
            is AuthState.Authenticated -> {
                flow {
                    // Mock remote fetch (this would be your Convex query)
                    delay(500)
                    
                    val prefs = dataStore.data.first()
                    val existingId = prefs[KEY_USER_ID]
                    
                    // Simulate fetching a user that exists remotely
                    if (existingId == authState.userId) {
                        val mockRemoteUser = User(
                            id = authState.userId,
                            name = prefs[KEY_USER_NAME],
                            email = prefs[KEY_USER_EMAIL],
                            gender = prefs[KEY_USER_GENDER]
                        )
                        emit(Ok(UserState.Registered(mockRemoteUser)))
                    } else {
                        // Simulate User Not Found remotely
                        emit(Err(AppError.NotFound))
                    }
                }
            }
            else -> emptyFlow()
        }
    }

    companion object {
        val KEY_USER_ID = stringPreferencesKey("user_id")
        val KEY_USER_NAME = stringPreferencesKey("user_name")
        val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        val KEY_USER_GENDER = stringPreferencesKey("user_gender")
    }

    init {
        // Collect from the reactive remote stream and persist it to DataStore.
        scope.launch {
            remoteState.collect { result ->
                result.onSuccess { userState ->
                    if (userState is UserState.Registered) {
                        dataStore.edit { prefs ->
                            prefs[KEY_USER_ID] = userState.user.id
                            userState.user.name?.let { prefs[KEY_USER_NAME] = it }
                            userState.user.email?.let { prefs[KEY_USER_EMAIL] = it }
                            userState.user.gender?.let { prefs[KEY_USER_GENDER] = it }
                        }
                    }
                }.onFailure { error ->
                    when (error) {
                        is AppError.NotFound -> {
                            // In reality, if user was deleted remotely, we'd clear the cache
                            dataStore.edit { prefs ->
                                prefs.remove(KEY_USER_ID)
                                prefs.remove(KEY_USER_NAME)
                                prefs.remove(KEY_USER_EMAIL)
                                prefs.remove(KEY_USER_GENDER)
                            }
                        }
                        is AppError.Network -> {
                            // Ignore network errors, let local-first cache serve the UI
                        }
                        is AppError.Unknown -> {}
                    }
                }
            }
        }
    }

    // This is the ONLY state exposed to the app. 
    // It instantly emits whatever is in the local DataStore cache.
    override val state: Flow<UserState> = dataStore.data.map { prefs ->
        val id = prefs[KEY_USER_ID]
        if (id != null) {
            UserState.Registered(
                User(
                    id = id,
                    name = prefs[KEY_USER_NAME],
                    email = prefs[KEY_USER_EMAIL],
                    gender = prefs[KEY_USER_GENDER]
                )
            )
        } else {
            // No cached user. They might need to register, or we might still be fetching.
            UserState.Checking
        }
    }

    override suspend fun createNewUser(userId: String): Result<Unit, AppError> {
        val token = authRepository.getCurrentToken()
        if (token == null) {
            return Err(AppError.Unknown(IllegalStateException("No auth token available")))
        }
        
        return try {
            // Mock remote creation (Convex mutation)
            delay(500)
            
            // Force save to datastore so our local state knows we are registered now
            val newUser = User(id = userId)
            dataStore.edit { prefs ->
                 prefs[KEY_USER_ID] = newUser.id
            }
            Ok(Unit)
        } catch (e: Exception) {
            Err(AppError.Network)
        }
    }

    override suspend fun updateUser(user: User): Result<Unit, AppError> {
        return try {
            // Mock remote update (Convex mutation)
            delay(500)
            
            // Force save to datastore 
            dataStore.edit { prefs ->
                 prefs[KEY_USER_ID] = user.id
                 user.name?.let { prefs[KEY_USER_NAME] = it }
                 user.email?.let { prefs[KEY_USER_EMAIL] = it }
                 user.gender?.let { prefs[KEY_USER_GENDER] = it }
            }
            Ok(Unit)
        } catch (e: Exception) {
            Err(AppError.Network)
        }
    }
}
