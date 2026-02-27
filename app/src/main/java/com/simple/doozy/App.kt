package com.simple.doozy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simple.doozy.feature.auth.AuthState
import com.simple.doozy.navigation.AuthenticatedNav
import com.simple.doozy.navigation.UnauthenticatedNav
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun App(modifier: Modifier = Modifier) {
    val mainViewModel = koinViewModel<MainViewModel>()
    val authState by mainViewModel.authState.collectAsStateWithLifecycle()
    val onboardingCompleted by mainViewModel.onboardingCompleted.collectAsStateWithLifecycle()

    // if AuthState.Checking -> splash screen
    if (authState is AuthState.Authenticated) {
        AuthenticatedNav(modifier)
    } else {
        UnauthenticatedNav(modifier, onboardingCompleted)
    }

}


