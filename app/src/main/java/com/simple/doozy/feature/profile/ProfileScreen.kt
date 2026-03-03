package com.simple.doozy.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simple.doozy.common.ui.util.AppPreview
import com.simple.doozy.common.ui.util.shimmerEffect
import com.simple.doozy.feature.auth.model.User
import com.simple.doozy.ui.theme.PremiumGold
import com.simple.doozy.ui.theme.ScreenPaddingValues

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToSubscribeFlow: () -> Unit,
    onNavigateToActiveSubscriptionPage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ProfileScreenContent(
        state = state,
        onAction = { action ->
            when (action) {
                ProfileUiAction.EditProfileClicked -> onNavigateToEditProfile()
                ProfileUiAction.LogoutClicked -> viewModel.logout()
                is ProfileUiAction.DummyButtonClicked -> { /* Handle dummy later */
                }

                ProfileUiAction.SubscribeClicked -> onNavigateToSubscribeFlow()
                ProfileUiAction.ManageSubscriptionClicked -> onNavigateToActiveSubscriptionPage()
            }
        },
        modifier = modifier
    )
}

@Composable
private fun ProfileScreenContent(
    state: ProfileState,
    onAction: (ProfileUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(ScreenPaddingValues)
    ) {
        Spacer(Modifier.height(32.dp))

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (state.user != null) {
            val user = state.user

            // Header: Avatar & Info
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.Person,
                        contentDescription = "User Avatar",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.width(20.dp))
                Column {
                    Text(
                        user.metadata?.name ?: "Anonymous User",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(Modifier.height(4.dp))
                    if (user.metadata?.email != null) {
                        Text(
                            user.metadata.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        InputChip(
                            selected = true,
                            onClick = { onAction(ProfileUiAction.EditProfileClicked) },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "Setup your profile",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                        contentDescription = "Edit Profile",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            },
                            colors = InputChipDefaults.inputChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.onSurface,
                                selectedLabelColor = MaterialTheme.colorScheme.surface
                            ),
                            border = null
                        )
                    }
                }
            }

            Spacer(Modifier.height(40.dp))

            // Subscription
            when (val subState = state.subscription) {
                is com.simple.doozy.feature.subscription.data.SubscriptionState.Checking -> {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = PremiumGold.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PremiumGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .height(14.dp)
                                        .width(80.dp)
                                        .clip(MaterialTheme.shapes.small)
                                        .shimmerEffect()
                                )
                                Spacer(Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .height(20.dp)
                                        .width(140.dp)
                                        .clip(MaterialTheme.shapes.small)
                                        .shimmerEffect()
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .height(24.dp)
                                    .width(60.dp)
                                    .clip(MaterialTheme.shapes.extraLarge)
                                    .shimmerEffect()
                            )
                        }
                    }
                    Spacer(Modifier.height(32.dp))
                }

                is com.simple.doozy.feature.subscription.data.SubscriptionState.NoSubscription -> {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAction(ProfileUiAction.SubscribeClicked) }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "Upgrade to Premium",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Unlock all features",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                contentDescription = "Upgrade",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(Modifier.height(32.dp))
                }

                is com.simple.doozy.feature.subscription.data.SubscriptionState.Subscribed -> {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = PremiumGold.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PremiumGold),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAction(ProfileUiAction.ManageSubscriptionClicked) }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "Subscription",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    subState.plan.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            Badge(
                                containerColor = PremiumGold,
                                contentColor = Color.White
                            ) {
                                Text("Active", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                    }
                    Spacer(Modifier.height(32.dp))
                }
            }

            // General section
            Text(
                "General",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))

            ProfileMenuItem(
                icon = Icons.Rounded.Person,
                title = "Edit Profile",
                onClick = { onAction(ProfileUiAction.EditProfileClicked) }
            )
            ProfileMenuItem(
                icon = Icons.Rounded.Notifications,
                title = "Notifications",
                onClick = { onAction(ProfileUiAction.DummyButtonClicked("Notifications")) }
            )
            ProfileMenuItem(
                icon = Icons.Rounded.Lock,
                title = "Privacy & Security",
                onClick = { onAction(ProfileUiAction.DummyButtonClicked("Privacy & Security")) }
            )
            ProfileMenuItem(
                icon = Icons.Rounded.Info,
                title = "Help & Support",
                onClick = { onAction(ProfileUiAction.DummyButtonClicked("Help & Support")) }
            )

            Spacer(Modifier.height(24.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            Spacer(Modifier.height(24.dp))

            ProfileMenuItem(
                icon = Icons.AutoMirrored.Rounded.ExitToApp,
                title = "Log Out",
                titleColor = MaterialTheme.colorScheme.error,
                onClick = { onAction(ProfileUiAction.LogoutClicked) }
            )
        }

        Spacer(Modifier.height(32.dp))

        Text(
            "Version 2.4.0 (Build 1042)",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(32.dp))
    }
}

sealed interface ProfileUiAction {
    data object EditProfileClicked : ProfileUiAction
    data object LogoutClicked : ProfileUiAction
    data class DummyButtonClicked(val buttonName: String) : ProfileUiAction
    data object SubscribeClicked : ProfileUiAction
    data object ManageSubscriptionClicked : ProfileUiAction
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    titleColor: Color = MaterialTheme.colorScheme.onBackground,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = titleColor.copy(alpha = 0.8f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            title,
            style = MaterialTheme.typography.bodyLarge,
            color = titleColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}

@PreviewLightDark
@Composable
private fun ProfileScreenPreview() {
    AppPreview {
        ProfileScreenContent(
            state = ProfileState(
                user = User.MOCK,
                subscription = com.simple.doozy.feature.subscription.data.SubscriptionState.Subscribed(
                    com.simple.doozy.feature.subscription.data.SubscriptionPlan("pro", "Pro Monthly", "$9.99"),
                    "2026-03-02", "2026-04-02", "2026-04-02"
                ),
                isLoading = false
            ),
            onAction = {}
        )
    }
}