package com.simple.doozy.feature.profile

import com.simple.doozy.feature.auth.data.AuthRepository
import com.simple.doozy.feature.session.UserSessionClearable

class LogoutUseCase(
    private val authRepository: AuthRepository,
    private val clearables: List<UserSessionClearable>
) {
    suspend operator fun invoke() {
        clearables.forEach { it.clearSessionData() }
        authRepository.logout()
    }
}
