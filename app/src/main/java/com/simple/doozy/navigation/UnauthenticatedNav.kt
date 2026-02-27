package com.simple.doozy.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.simple.doozy.feature.auth.screens.LoginScreen
import com.simple.doozy.feature.auth.screens.LoginViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UnauthenticatedNav(modifier: Modifier, onboardingCompleted: Boolean) {

    if (!onboardingCompleted) {
        OnboardingNav(modifier)
        return
    }
    val backstack = rememberNavBackStack(Route.Unauthenticated.Authentication.Login)

    NavDisplay(
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        backStack = backstack,
        onBack = { backstack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Route.Unauthenticated.Authentication.Login> {
                val viewModel = koinViewModel<LoginViewModel>()
                val state by viewModel.state.collectAsStateWithLifecycle()
                LoginScreen(
                    modifier = modifier,
                    state = state,
                    onPhoneChange = viewModel::updatePhoneNumber,
                    navigateToOtp = { phone ->
                        if (viewModel.validatePhone()) {
                            backstack.add(Route.Unauthenticated.Authentication.OtpVerification(phone))
                        }
                    }
                )
            }
            entry<Route.Unauthenticated.Authentication.OtpVerification> {
                val route = it as Route.Unauthenticated.Authentication.OtpVerification
                val viewModel = koinViewModel<com.simple.doozy.feature.auth.screens.OtpViewModel>()
                val state by viewModel.state.collectAsStateWithLifecycle()
                com.simple.doozy.feature.auth.screens.OtpScreen(
                    modifier = modifier,
                    state = state,
                    phoneNumber = route.phoneNumber,
                    verify = viewModel::verifyOtp,
                    onBack = { backstack.removeLastOrNull() }
                )
            }
        }
    )
}


