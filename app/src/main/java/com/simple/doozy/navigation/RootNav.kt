package com.simple.doozy.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.simple.doozy.MainViewModel
import com.simple.doozy.feature.session.SessionState
import com.simple.doozy.navigation.route.Route
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RootNav(modifier: Modifier) {
    val viewModel = koinViewModel<MainViewModel>()
    val sessionState by viewModel.sessionState.collectAsStateWithLifecycle()
    if (sessionState is SessionState.Checking) return
    val rootDestination by remember { derivedStateOf { if (sessionState is SessionState.Unauthenticated) Route.UnauthenticatedNav else Route.AuthenticatedNav } }
    val backstack = rememberNavBackStack(rootDestination)

    LaunchedEffect(rootDestination) {
        if (rootDestination != backstack.lastOrNull()) {
            backstack.clear()
            backstack.add(rootDestination)
        }
    }

    NavDisplay(
        backStack = backstack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        onBack = { if (backstack.size > 1) backstack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Route.AuthenticatedNav> {
                AuthenticatedNav(modifier)
            }
            entry<Route.UnauthenticatedNav> {
                UnauthenticatedNav(modifier)
            }
        }
    )
}