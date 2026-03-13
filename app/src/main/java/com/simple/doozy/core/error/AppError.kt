package com.simple.doozy.core.error

sealed class AppError : Exception() {
    data object Network : AppError()
    data object NotFound : AppError()
    data class Unknown(override val cause: Throwable? = null) : AppError()
}
