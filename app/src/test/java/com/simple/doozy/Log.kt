package com.simple.doozy

import kotlin.time.Clock.System.now
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
inline fun <reified T : Any> T.log(msg: String) = println("${now()} - [${this::class.simpleName}] $msg")
