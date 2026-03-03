package com.simple.doozy.common.ui.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class SnackbarController {

    private val _messages = MutableSharedFlow<SnackbarMessage>(extraBufferCapacity = 1)
    val messages = _messages.asSharedFlow()

    fun showMessage(message: String, actionLabel: String? = null) {
        _messages.tryEmit(SnackbarMessage(message, actionLabel))
    }

    data class SnackbarMessage(
        val message: String,
        val actionLabel: String? = null
    )
}
