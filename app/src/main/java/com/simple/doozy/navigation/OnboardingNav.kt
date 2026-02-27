package com.simple.doozy.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.simple.doozy.feature.onboarding.OnboardingScreen
import com.simple.doozy.feature.onboarding.OnboardingViewModel
import org.koin.compose.viewmodel.koinViewModel
import java.util.Map.entry

@Composable
fun OnboardingNav(modifier: Modifier) {

    val backstack = rememberNavBackStack(Route.Unauthenticated.Onboarding)

    NavDisplay(
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        backStack = backstack,
        onBack = { backstack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Route.Unauthenticated.Onboarding> { route ->
                val viewModel = koinViewModel<OnboardingViewModel>()
                OnboardingScreen(modifier, viewModel::setOnboardingComplete)
            }
        }
    )
}
