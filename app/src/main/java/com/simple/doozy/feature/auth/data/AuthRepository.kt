package com.simple.doozy.feature.auth.data

import android.app.Activity
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.simple.doozy.feature.auth.AuthState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

sealed class AuthError(override val message: String) : Exception(message) {
    class InvalidOtp : AuthError("Invalid OTP Code.")
    class InvalidPhoneNumber : AuthError("Invalid phone number format.")
    class TooManyRequests : AuthError("Too many requests. Please try again later.")
    class Timeout : AuthError("OTP verification timed out.")
    class NetworkError : AuthError("Network error. Please check your connection.")
    class CodeSentFailed : AuthError("Failed to send code.")
    class Unknown(message: String) : AuthError(message)
}

interface AuthRepository {
    val state: Flow<AuthState>
    val resendCountdown: Flow<Int>

    suspend fun sendOtp(phoneNumber: String, activity: Activity): Result<Unit, AuthError>
    suspend fun loginWithOtp(otp: String): Result<Unit, AuthError>
    suspend fun logout()
    fun getCurrentToken(): String?
}

class DefaultAuthRepository : AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val scope = CoroutineScope(Dispatchers.Default)
    private val loginMutex = Mutex()

    private val _resendCountdown: MutableStateFlow<Int> = MutableStateFlow(0)
    override val resendCountdown: Flow<Int> = _resendCountdown.asStateFlow()

    private var countdownJob: Job? = null
    private var verificationId: String? = null
    private val resendTokens = mutableMapOf<String, PhoneAuthProvider.ForceResendingToken>()

    override val state: StateFlow<AuthState> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {
                user.getIdToken(false).addOnSuccessListener { result ->
                    trySend(AuthState.Authenticated(user.uid, result.token ?: ""))
                }.addOnFailureListener { trySend(AuthState.Unauthenticated) }
            } else {
                trySend(AuthState.Unauthenticated)
            }
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }.stateIn(scope, WhileSubscribed(5000), AuthState.Checking)

    override suspend fun sendOtp(phoneNumber: String, activity: Activity): Result<Unit, AuthError> {
        verificationId = null
        return suspendCancellableCoroutine { continuation ->
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    scope.launch {
                        try {
                            loginMutex.withLock {
                                firebaseAuth.signInWithCredential(credential).await()
                                // AuthStateListener will pick up the change
                            }
                            if (continuation.isActive) {
                                continuation.resume(Ok(Unit))
                            }
                        } catch (e: Exception) {
                            if (continuation.isActive) {
                                continuation.resume(Err(mapFirebaseError(e)))
                            }
                        }
                    }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    if (continuation.isActive) {
                        continuation.resume(Err(mapFirebaseError(e)))
                    }
                }

                override fun onCodeSent(
                    verifiedVerificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    verificationId = verifiedVerificationId
                    resendTokens[phoneNumber] = token
                    startCountdown()
                    if (continuation.isActive) {
                        continuation.resume(Ok(Unit))
                    }
                }
            }

            val optionsBuilder = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(30L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)

            resendTokens[phoneNumber]?.let { token ->
                optionsBuilder.setForceResendingToken(token)
            }

            PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
        }
    }

    override suspend fun loginWithOtp(otp: String): Result<Unit, AuthError> = loginMutex.withLock {
        if (verificationId == null) {
            return Err(AuthError.CodeSentFailed())
        }
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
            firebaseAuth.signInWithCredential(credential).await()
            // AuthStateListener picks this up and updates state
            Ok(Unit)
        } catch (e: Exception) {
            Err(mapFirebaseError(e))
        }
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        _resendCountdown.value = 30
        countdownJob = scope.launch {
            while (_resendCountdown.value > 0) {
                delay(1000)
                _resendCountdown.update { it - 1 }
            }
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
        // AuthStateListener picks this up and updates state
    }

    override fun getCurrentToken(): String? {
        val currentState = state.value
        return if (currentState is AuthState.Authenticated) {
            currentState.authToken
        } else {
            null
        }
    }

    private fun mapFirebaseError(e: Exception): AuthError {
        val message = e.message ?: ""
        return when {
            message.contains("invalid-verification-code") -> AuthError.InvalidOtp()
            message.contains("invalid-phone-number") -> AuthError.InvalidPhoneNumber()
            message.contains("too-many-requests") -> AuthError.TooManyRequests()
            message.contains("network-request-failed") -> AuthError.NetworkError()
            message.contains("timeout") -> AuthError.Timeout()
            else -> AuthError.Unknown(message)
        }
    }
}
