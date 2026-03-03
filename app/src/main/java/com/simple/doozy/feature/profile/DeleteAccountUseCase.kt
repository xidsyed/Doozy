package com.simple.doozy.feature.profile

import com.simple.doozy.feature.auth.data.AuthRepository
import com.simple.doozy.feature.user.data.UserRepository
import kotlinx.coroutines.delay

class DeleteAccountUseCase(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        // Mocking an API call delay
        delay(1000)
        // For a real app, this would delete local DB entries, remote user data, and cancel subscriptions.
        // For now we mock data deletion by doing a clean logout.
        authRepository.logout()
    }
}
