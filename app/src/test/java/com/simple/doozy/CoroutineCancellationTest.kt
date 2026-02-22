package com.simple.doozy

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Test
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Clock.System.now
import kotlin.time.ExperimentalTime

class CoroutineCancellationTest {

    @OptIn(ExperimentalTime::class)
    fun log(msg: String) = println("${now()} - [${Thread.currentThread().name}] $msg")
    @Test
    fun main() = runBlocking {
        log("=== START ===")

//         Uncomment one at a time to experiment

//         basicParentChildCancellation()
//         childCancelDoesNotCancelParent()
//         childFailureCancelsParent()
//         supervisorPreventsUpwardCancellation()
//         cancelAndJoinVsJoin()
//         cooperativeCancellation()
//         nonCooperativeBusyLoop()
//        cooperativeBusyLoop()
//         ensureActiveVsIsActive()
//         withTimeoutExample()
//         withTimeoutOrNullExample()
//         nonCancellableInFinally()
//         asyncCancellation()
//         structuredConcurrencyScopeCancel()
//         nestedScopeCancelPropagation()

        log("=== END ===")
    }

    /**
     * 1️⃣ Parent cancels → all children cancel
     */
    suspend fun basicParentChildCancellation() = coroutineScope {
        log("basicParentChildCancellation")

        val parent = launch {
            launch {
                try {
                    repeat(5) {
                        log("Child running $it")
                        delay(500)
                    }
                } finally {
                    log("Child finally block")
                }
            }
        }

        delay(1000)
        log("Cancel parent")
        parent.cancel()
        parent.join()
    }

    /**
     * 2️⃣ Cancelling child does NOT cancel parent
     */
    suspend fun childCancelDoesNotCancelParent() = coroutineScope {
        log("childCancelDoesNotCancelParent")

        val parent = launch {
            val child = launch {
                delay(500)
                log("Child finishing")
            }

            delay(200)
            log("Cancelling child")
            child.cancel()

            launch {
                delay(200)
                log("Child2 throws cancellation exception")
                throw CancellationException()
            }

            delay(1000)
            log("Parent still alive")
        }

        parent.join()
    }

    /**
     * 3️⃣ Child failure (Exception) cancels parent (regular Job)
     */
    suspend fun childFailureCancelsParent() = coroutineScope {
        log("childFailureCancelsParent")

        try {
            launch {
                launch {
                    delay(500)
                    throw RuntimeException("Boom")
                }
                delay(2000)
                log("Parent completed")
            }.join()
        } catch (e: Exception) {
            log("Caught $e")
        }
    }

    /**
     * 4️⃣ Supervisor prevents upward cancellation
     */
    suspend fun supervisorPreventsUpwardCancellation() = supervisorScope {
        log("supervisorPreventsUpwardCancellation")

        launch {
            throw RuntimeException("Failure inside supervisor child")
        }

        launch {
            delay(1000)
            log("Sibling still runs")
        }
    }

    /**
     * 5️⃣ cancelAndJoin vs join
     */
    suspend fun cancelAndJoinVsJoin() = coroutineScope {
        val job = launch {
            try {
                delay(5000)
            } finally {
                log("Cleanup")
            }
        }

        delay(1000)
        job.cancelAndJoin() // cancel + wait
        log("After cancelAndJoin")
    }

    /**
     * 6️⃣ Cooperative cancellation (delay is cancellable)
     */
    suspend fun cooperativeCancellation() = coroutineScope {
        val job = launch {
            repeat(1000) {
                delay(100)
                log("Running $it")
            }
        }

        delay(500)
        job.cancel()
    }

    /**
     * 7️⃣ Non-cooperative cancellation (busy loop)
     */
    suspend fun nonCooperativeBusyLoop() = coroutineScope {
        val job = launch(Dispatchers.IO) {
            log("Non Cooperative Coroutine Launched ")
            while (true) {
                // NO suspension point → won't cancel!
            }
        }

        log("parent coroutine about to suspend")
        delay(500)
        // UNREACHABLE in `runBlocking` if launch does not have a different dispatcher.
        log("Trying to cancel")
        job.cancel()
    }

    /**
     * Cooperative cancellation (busy loop)
     */
    suspend fun cooperativeBusyLoop() = coroutineScope {
        val job = launch(Dispatchers.IO) {
            log("Cooperative Coroutine Launched ")
            while (isActive) {

            }
        }

        log("parent coroutine about to suspend")
        delay(500)
        log("Trying to cancel")
        job.cancel()
    }

    /**
     * 8️⃣ ensureActive vs isActive
     */
    suspend fun ensureActiveVsIsActive() = coroutineScope {
        val job = launch {
            repeat(1000) {
                ensureActive() // throws CancellationException if canceled
                delay(1000)
            }
        }

        delay(500)
        job.cancel()
    }

    /**
     * 9️⃣ withTimeout (throws TimeoutCancellationException)
     */
    suspend fun withTimeoutExample() {
        try {
            withTimeout(1000) {
                repeat(5) {
                    delay(500)
                    log("Working $it")
                }
            }
        } catch (e: TimeoutCancellationException) {
            log("Timeout!")
        }
    }

    /**
     * 🔟 withTimeoutOrNull
     */
    suspend fun withTimeoutOrNullExample() {
        val result = withTimeoutOrNull(1000) {
            delay(2000)
            "Done"
        }
        log("Result = $result") // null
    }

    /**
     * 1️⃣1️⃣ NonCancellable in finally
     */
    suspend fun nonCancellableInFinally() = coroutineScope {
        val job = launch(Dispatchers.IO) {
            try {
                delay(5000)
            } finally {
                withContext(NonCancellable) {
                    delay(1000)
                    log("Ran cleanup safely")
                }
            }
        }

        delay(1000)
        job.cancelAndJoin()
    }

    /**
     * 1️⃣2️⃣ async cancellation
     */
    suspend fun asyncCancellation() = supervisorScope {

        // ─────────────────────────────────────────────
        // 1️⃣ launch + join()
        // ─────────────────────────────────────────────
        val job = launch(Dispatchers.IO) {
            throw Exception("OOPS from launch")
        }

        try {
            job.join()
            // ✅ join() only waits for completion
            // ❗ It does NOT rethrow the exception
            // ❗ It does NOT cancel supervisorScope
        } catch (e: Exception) {
            println("This will NOT run")
        }

        log("after join")
        // ✅ runs — supervisorScope is still active


        // ─────────────────────────────────────────────
        // 2️⃣ async + await()
        // ─────────────────────────────────────────────
        val deferred = async(Dispatchers.IO) {
            throw Exception("OOPS from async")
        }

        try {
            deferred.await()
            // ❗ await() DOES rethrow the exception
            // because async is meant to produce a result
        } catch (e: Exception) {
            println("Caught from await")
            // ✅ runs
        }

        log("after await")
        // ✅ still runs — exception was caught,
        // and supervisorScope was never cancelled


        // ─────────────────────────────────────────────
        // 3️⃣ Scope is still healthy
        // ─────────────────────────────────────────────
        launch {
            log("new coroutine launches fine")
        }

        delay(200)
        log("end reached")
    }


    /**
     * 1️⃣3️⃣ Cancel entire scope
     */
    suspend fun structuredConcurrencyScopeCancel() = coroutineScope {
        val scope = this

        launch {
            delay(2000)
            log("Child1")
        }

        launch {
            delay(3000)
            log("Child2")
        }

        delay(1000)
        log("Cancel scope")
        scope.cancel()
    }

    /**
     * 1️⃣4️⃣ Nested scope propagation
     */
    suspend fun nestedScopeCancelPropagation() = coroutineScope {
        val parent = launch {
            coroutineScope {
                launch {
                    delay(5000)
                }
            }
        }

        delay(1000)
        parent.cancel()
    }

}