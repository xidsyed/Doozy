package com.simple.doozy.common.ui

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun defaultButtonColors() = ButtonDefaults.buttonColors(
    containerColor = MaterialTheme.colorScheme.onBackground,
    contentColor = MaterialTheme.colorScheme.background,
    disabledContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
)