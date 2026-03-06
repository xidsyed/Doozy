package com.simple.doozy.common

fun <T> MutableList<T>.removeSecondLastOrNull() = if (this.size > 1) removeLastOrNull() else null