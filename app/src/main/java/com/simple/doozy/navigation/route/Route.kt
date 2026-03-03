package com.simple.doozy.navigation.route

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {

    @Serializable
    data object UnauthenticatedNav : Route {

        @Serializable
        sealed interface Authentication : Route {
            @Serializable
            data object Login : Authentication

            @Serializable
            data class OtpVerification(val phoneNumber: String) : Authentication

            @Serializable
            data object Registration : Route
        }
    }

    @Serializable
    data object AuthenticatedNav : Route {

        @Serializable
        data object HomeNav : Route {
            /**
             * Destinations on this level will be switched-to using if-else logic.
             *
             * There will not be a `HomeNav` navDisplay that uses entryProviders to switch between
             * top-level destinations. This is so that their view-models are not
             * cleared when navigating between these two top-level destinations
             * */

            @Serializable
            sealed interface BottomNavTab

            @Serializable
            data object TodosTab : Route, BottomNavTab {
                @Serializable
                data object TodosList : Route

                @Serializable
                data class TodoDetail(val todoId: String?) : Route
            }

            @Serializable
            data object ProfileTab : Route, BottomNavTab {

                @Serializable
                data object Profile : Route

                @Serializable
                data object EditProfile : Route

                @Serializable
                data object AccountPrivacy : Route

                @Serializable
                data object Support : Route

            }
        }


        @Serializable
        data object SubscribeNav : Route {

            @Serializable
            data object Checkout : Route

        }

        @Serializable
        data object SubscriptionStatus : Route

        @Serializable
        data object Onboarding : Route

    }
}