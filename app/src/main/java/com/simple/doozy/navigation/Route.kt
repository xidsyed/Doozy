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
            data object Registration : Authenticated
        }


    }

    sealed interface Authenticated : Route {
        @Serializable
        object Todos : Authenticated

        @Serializable
        data class TodoDetail(val todoId: String) : Authenticated

        @Serializable
        data object Profile : Authenticated
    }
}