package com.simple.doozy.feature.user.data

import com.simple.doozy.feature.auth.model.User

sealed interface UserState {
    data object Checking : UserState
    data object NotFound : UserState
    data class Registered(val user: User) : UserState
}
