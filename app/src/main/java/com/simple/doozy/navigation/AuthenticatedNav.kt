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
                    onNavigateToSubscribeFlow = { backstack.add(AuthenticatedNav.SubscribeNav) },
                    onNavigateToActiveSubscriptionPage = { backstack.add(AuthenticatedNav.ActiveSubscriptionDetails) }
                )
            }

            entry<AuthenticatedNav.SubscribeNav> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Subscription Flow (SubscriptionPlans, Checkout)")
                }
            }

            entry<AuthenticatedNav.ActiveSubscriptionDetails> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Active Subscription Details")
                }
            }

            entry<AuthenticatedNav.Onboarding> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Onboarding Flow")
                }
            }
        }
    )
}