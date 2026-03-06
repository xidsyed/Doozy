package com.simple.doozy.feature.subscription.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.simple.doozy.feature.subscription.data.SubscriptionRepository
import com.simple.doozy.feature.subscription.data.SubscriptionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class PaymentUiState(
    val title: String = "Processing Payment",
    val description: String = "Please wait...",
    val isError: Boolean = false,
    val isComplete: Boolean = false,
    val navigateSuccess: Boolean = false,
    val navigateFailure: Boolean = false
)

class PaymentViewModel(
    private val paymentRepository: PaymentRepository,
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private var hasInitiated = false

    init {
        viewModelScope.launch {
            combine(
                paymentRepository.paymentState,
                subscriptionRepository.fetchUserSubscription()
            ) { checkoutState, subscriptionState ->
                Pair(checkoutState, subscriptionState)
            }.collect { (checkoutState, subscriptionState) ->
                when (checkoutState) {
                    is PaymentState.Idle -> {
                        _uiState.value = PaymentUiState(title = "Initializing")
                    }

                    is PaymentState.Initiating -> {
                        _uiState.value = PaymentUiState(
                            title = "Connecting",
                            description = "Starting secure connection..."
                        )
                    }

                    is PaymentState.Ongoing -> {
                        _uiState.value = PaymentUiState(
                            title = "Processing Payment",
                            description = "Confirming your transaction..."
                        )
                    }

                    is PaymentState.Failure -> {
                        _uiState.value = PaymentUiState(
                            title = "Payment Failed",
                            description = "Redirecting in 3..2..1...",
                            isError = true
                        )
                        kotlinx.coroutines.delay(3000)
                        _uiState.value = _uiState.value.copy(navigateFailure = true)
                    }

                    is PaymentState.Success -> {
                        if (subscriptionState is SubscriptionState.Subscribed) {
                            _uiState.value = PaymentUiState(
                                title = "Success!",
                                description = "Subscription activated.",
                                isComplete = true
                            )
                            kotlinx.coroutines.delay(1000)
                            _uiState.value = _uiState.value.copy(navigateSuccess = true)
                        } else {
                            _uiState.value = PaymentUiState(
                                title = "Completing Order",
                                description = "Finalizing subscription..."
                            )
                        }
                    }
                }
            }
        }
    }

    fun initiateCheckout(orderId: String) {
        if (!hasInitiated) {
            hasInitiated = true
            viewModelScope.launch {
                paymentRepository.initiate(orderId)
            }
        }
    }

    fun ackNavigation() {
        paymentRepository.resetPaymentState()
    }
}

@Composable
fun PaymentScreen(
    orderId: String,
    viewModel: PaymentViewModel,
    onNavigateSuccess: () -> Unit,
    onNavigateFailure: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(orderId) {
        viewModel.initiateCheckout(orderId)
    }

    LaunchedEffect(uiState.navigateSuccess, uiState.navigateFailure) {
        if (uiState.navigateSuccess) {
            viewModel.ackNavigation()
            onNavigateSuccess()
        } else if (uiState.navigateFailure) {
            viewModel.ackNavigation()
            onNavigateFailure()
        }
    }

    PaymentScreenContent(uiState = uiState, modifier = modifier)
}

@Composable
private fun PaymentScreenContent(
    uiState: PaymentUiState,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (uiState.isComplete) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = "Success",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
            } else if (uiState.isError) {
                Icon(
                    imageVector = Icons.Rounded.Warning,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(64.dp)
                )
            } else {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = uiState.title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = uiState.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}

