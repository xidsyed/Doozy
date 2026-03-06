package com.simple.doozy.common

fun <T> MutableList<T>.removeLastIfMultiple() = if (this.size > 1) removeLastOrNull() else null