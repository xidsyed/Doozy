package com.simple.doozy.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.simple.doozy.feature.subscription.checkout.CheckoutScreen
import com.simple.doozy.feature.subscription.status.SubscriptionStatusScreen
import com.simple.doozy.navigation.route.Route.AuthenticatedNav


@Composable
fun AuthenticatedNav(modifier: Modifier) {
    val backstack = rememberNavBackStack(AuthenticatedNav.HomeNav)

    NavDisplay(
        backStack = backstack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        onBack = { backstack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<AuthenticatedNav.HomeNav> {
                HomeNav(
                    modifier = modifier,
                    onNavigateToSubscribeFlow = { backstack.add(AuthenticatedNav.SubscribeNav.Checkout) },
                    onNavigateToActiveSubscriptionPage = { backstack.add(AuthenticatedNav.SubscriptionStatus) }
                )
            }

            entry<AuthenticatedNav.SubscribeNav.Checkout> {
                CheckoutScreen(
                    modifier = modifier,
                    onBack = { backstack.removeLastOrNull() },
                    onSubscribe = {
                        backstack.removeLastOrNull() // Remove checkout from backstack
                        backstack.add(AuthenticatedNav.SubscriptionStatus)
                    }
                )
            }

            entry<AuthenticatedNav.SubscriptionStatus> {
                SubscriptionStatusScreen(
                    modifier = modifier,
                    onReturnHome = {
                        backstack.clear()
                        backstack.add(AuthenticatedNav.HomeNav)
                    }
                )
            }

            entry<AuthenticatedNav.Onboarding> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Onboarding Flow")
                }
            }
        }
    )
}