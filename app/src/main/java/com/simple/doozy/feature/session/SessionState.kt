package com.simple.doozy.feature.session

import com.simple.doozy.feature.auth.model.User
import com.simple.doozy.feature.subscription.data.SubscriptionRepositoryState

sealed interface SessionState {
    data object Checking : SessionState
    data object Unauthenticated : SessionState
    data class Authenticated(val userId: String) : SessionState
    data class Registered(val user: User, val subscription: SubscriptionRepositoryState) : SessionState
}
