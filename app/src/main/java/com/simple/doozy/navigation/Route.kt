package com.simple.doozy.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {

    @Serializable
    sealed interface Unauthenticated : Route {
        @Serializable
        data object Onboarding : Unauthenticated

        @Serializable
        sealed interface Authentication : Unauthenticated {
            @Serializable
            data object Login : Authentication

            @Serializable
            data class OtpVerification(val phoneNumber: String) : Authentication

            @Serializable
            data object Registration : Authenticated
        }


    }

    sealed interface Authenticated : Route {
        @Serializable
        sealed interface Todos : Authenticated {
            @Serializable
            data object TodosList : Todos

            @Serializable
            data class TodoDetail(val todoId: String?) : Todos
        }

        @Serializable
        data object Profile : Authenticated
    }
}