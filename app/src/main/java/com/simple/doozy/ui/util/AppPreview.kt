package com.simple.doozy.ui.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.simple.doozy.ui.theme.DoozyTheme

@Composable
fun AppPreview(content: @Composable () -> Unit) {
    DoozyTheme() {
        Scaffold() { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                content()
            }
        }
    }
}