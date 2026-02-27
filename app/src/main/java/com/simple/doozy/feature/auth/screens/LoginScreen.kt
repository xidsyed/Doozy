package com.simple.doozy.feature.auth.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simple.doozy.R
import com.simple.doozy.ui.theme.DefaultRoundedShape

@Composable
fun LoginScreen(modifier: Modifier, state: LoginState, login: () -> Unit) {
    Column(
        modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Doozy",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 54.sp
                )
            )
            Text("All do's all day")
        }
        Spacer(Modifier.height(32.dp))
        Column(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginGoogleButton(state.isLoading, login)
            Spacer(Modifier.height(16.dp))
            state.error?.let {
                Text(
                    "Error: $it",
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }


    }
}

@Composable
fun LoginGoogleButton(isLoading: Boolean, login: () -> Unit) {
    Row(
        modifier = Modifier
            .height(54.dp)
            .clip(DefaultRoundedShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(enabled = !isLoading, onClick = login)
            .padding(horizontal = 32.dp)
            .alpha(if (isLoading) 0.5f else 1f),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.height(16.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_google),
            contentDescription = "Google Icon",
            tint = Color.Unspecified
        )
        Spacer(Modifier.width(12.dp))
        Text(
            "Sign In With Google",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge
        )
    }
}