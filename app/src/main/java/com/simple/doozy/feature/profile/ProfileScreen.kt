package com.simple.doozy.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.simple.doozy.ui.theme.ScreenPaddingValues

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(ScreenPaddingValues)
            .padding(16.dp)
    ) {
        Text("Profile", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(32.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.Person,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Jane Doe", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Medium)
                Text(
                    "jane@example.com",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(48.dp))
        Text(
            "Account",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(16.dp))

        ProfileMenuItem(icon = Icons.Rounded.Edit, title = "Edit Profile")
        ProfileMenuItem(icon = Icons.Rounded.Notifications, title = "Notifications")

        Spacer(Modifier.height(32.dp))
        Text(
            "General",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(16.dp))

        ProfileMenuItem(icon = Icons.Rounded.Star, title = "Go Premium")
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        ProfileMenuItem(
            icon = Icons.AutoMirrored.Rounded.ExitToApp,
            title = "Sign Out",
            titleColor = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    titleColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = titleColor.copy(alpha = 0.7f), modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Text(title, style = MaterialTheme.typography.bodyLarge, color = titleColor, modifier = Modifier.weight(1f))
        Icon(
            Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}