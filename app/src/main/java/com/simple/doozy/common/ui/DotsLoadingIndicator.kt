package com.simple.doozy.common.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun DotsLoadingIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "Dots")

    val dot1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                0f at 0
                1f at 200
                0f at 400
                0f at 1000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "Dot1"
    )
    val dot2 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                0f at 0
                0f at 200
                1f at 400
                0f at 600
                0f at 1000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "Dot2"
    )
    val dot3 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                0f at 0
                0f at 400
                1f at 600
                0f at 800
                0f at 1000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "Dot3"
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val baseColor = MaterialTheme.colorScheme.background
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(baseColor.copy(alpha = 0.4f + (dot1 * 0.6f))))
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(baseColor.copy(alpha = 0.4f + (dot2 * 0.6f))))
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(baseColor.copy(alpha = 0.4f + (dot3 * 0.6f))))
    }
}