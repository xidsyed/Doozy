package com.simple.doozy

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.simple.doozy.common.ui.util.SnackbarController
import com.simple.doozy.navigation.RootNav
import com.simple.doozy.ui.theme.DoozyTheme
import org.koin.compose.koinInject

@Composable
fun App() {
    val snackbarController = koinInject<SnackbarController>()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        snackbarController.messages.collect { message ->
            snackbarHostState.showSnackbar(
                message = message.message,
                actionLabel = message.actionLabel
            )
        }
    }

    DoozyTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { innerPadding ->
            RootNav(Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding))
        }
    }
}



