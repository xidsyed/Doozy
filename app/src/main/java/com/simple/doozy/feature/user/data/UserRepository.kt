package com.simple.doozy.feature.user.data

import com.simple.doozy.feature.auth.data.AuthRepository
import com.simple.doozy.feature.auth.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface UserRepository {
    val state: Flow<UserState>
    suspend fun fetchUser(userId: String)
    suspend fun createNewUser(userId: String)
    suspend fun updateUser(user: User)
}

class DefaultUserRepository(
    private val authRepository: AuthRepository
) : UserRepository {
    private val _state: MutableStateFlow<UserState> = MutableStateFlow(UserState.Checking)
    override val state: Flow<UserState> = _state.asStateFlow()

    override suspend fun fetchUser(userId: String) {
        _state.update { UserState.Checking }
        delay(1000)
        val token = authRepository.getCurrentToken()
        if (token == null) {
            _state.update { UserState.NotFound }
            throw IllegalStateException("No auth token available")
        }

        // Mock throwing UserNotFoundException to simulate registration flow
        throw UserNotFoundException("User $userId not found in ConvexDB")
    }

    override suspend fun createNewUser(userId: String) {
        delay(1000)
        val token = authRepository.getCurrentToken()
        if (token == null) {
            throw IllegalStateException("No auth token available")
        }
        // Mocking successful creation
        val newUser = User(id = userId)
        _state.update { UserState.Registered(newUser) }
    }

    override suspend fun updateUser(user: User) {
        delay(1000)
        _state.update { UserState.Registered(user) }
    }
}

class UserNotFoundException(message: String) : Exception(message)
