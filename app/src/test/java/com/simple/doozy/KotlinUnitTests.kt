package com.simple.doozy

import org.junit.Test
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

class KotlinUnitTests {
    @Test
    fun `test delegate`() {
        val e = Example()
        println(e.p)
        e.p = "NEW"
        println(e.p)
    }

    @Test
    fun `test lazy`() {

        val lazyValue: String by lazy {
            println("computed!")
            "Hello"
        }

        log(lazyValue)
        log(lazyValue)
    }

    @Test
    fun `kotlin results`() {
        val e = Result.success("hello")
    }
}

// OTHERS

class Example {
    var p: String by Delegate()
    var name: String by Delegates.observable("<no name>") { prop, old, new ->
        println("$old -> $new")
    }
}


class Delegate {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return "$thisRef, thank you for delegating '${property.name}' to me!"
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        println("$value has been assigned to '${property.name}' in $thisRef.")
    }
}