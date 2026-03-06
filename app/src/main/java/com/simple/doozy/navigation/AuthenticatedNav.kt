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
import com.simple.doozy.common.removeLastIfMultiple
import com.simple.doozy.feature.subscription.checkout.CheckoutScreen
import com.simple.doozy.feature.subscription.checkout.PaymentScreen
import com.simple.doozy.feature.subscription.checkout.PaymentViewModel
import com.simple.doozy.feature.subscription.status.SubscriptionStatusScreen
import com.simple.doozy.navigation.route.Route.AuthenticatedNav
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun AuthenticatedNav(modifier: Modifier) {
    val backstack = rememberNavBackStack(AuthenticatedNav.HomeNav)

    NavDisplay(
        backStack = backstack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        onBack = { backstack.removeLastIfMultiple() },
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
                    onBack = { backstack.removeLastIfMultiple() },
                    onNavigateToPayment = { orderId ->
                        backstack.add(AuthenticatedNav.SubscribeNav.Payment(orderId))
                    }
                )
            }

            entry<AuthenticatedNav.SubscribeNav.Payment> { route ->
                val paymentViewModel = koinViewModel<PaymentViewModel>()
                PaymentScreen(
                    orderId = route.orderId,
                    viewModel = paymentViewModel,
                    onNavigateSuccess = {
                        backstack.removeLastIfMultiple() // Remove payment
                        backstack.removeLastIfMultiple() // Remove checkout
                        backstack.add(AuthenticatedNav.SubscriptionStatus)
                    },
                    onNavigateFailure = {
                        backstack.removeLastIfMultiple() // Go back to Checkout
                    },
                    modifier = modifier
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