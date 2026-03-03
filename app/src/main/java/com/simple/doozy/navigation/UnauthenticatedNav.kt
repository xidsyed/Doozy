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
import com.simple.doozy.feature.auth.screens.OtpScreen
import com.simple.doozy.feature.auth.screens.OtpViewModel
import com.simple.doozy.navigation.route.Route.UnauthenticatedNav
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UnauthenticatedNav(modifier: Modifier) {

    val backstack = rememberNavBackStack(UnauthenticatedNav.Authentication.Login)

    NavDisplay(
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        backStack = backstack,
        onBack = { backstack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<UnauthenticatedNav.Authentication.Login> {
                val viewModel = koinViewModel<LoginViewModel>()
                val state by viewModel.state.collectAsStateWithLifecycle()
                LoginScreen(
                    modifier = modifier,
                    state = state,
                    onPhoneChange = viewModel::updatePhoneNumber,
                    navigateToOtp = { phone ->
                        if (viewModel.validatePhone()) {
                            backstack.add(UnauthenticatedNav.Authentication.OtpVerification(phone))
                        }
                    }
                )
            }
            entry<UnauthenticatedNav.Authentication.OtpVerification> {
                val viewModel = koinViewModel<OtpViewModel>()
                val state by viewModel.state.collectAsStateWithLifecycle()

                androidx.compose.runtime.LaunchedEffect(it.phoneNumber) {
                    viewModel.initPhoneNumber(it.phoneNumber)
                }

                OtpScreen(
                    modifier = modifier,
                    state = state,
                    phoneNumber = it.phoneNumber,
                    verify = viewModel::verifyOtp,
                    resend = viewModel::resendOtp,
                    onOtpChange = viewModel::updateOtp,
                    onBack = { backstack.removeLastOrNull() }
                )
            }
        }
    )
}


