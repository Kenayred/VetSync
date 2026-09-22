package com.example.vetsync.vista.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.example.vetsync.vista.theme.VerdePrincipal

@Composable
fun LoadingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading_dots")

    // Lista para simular los 3 puntos
    val dots = listOf(0, 1, 2)

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ) {
        dots.forEach { index ->
            // Animación de escala (crece y se encoge) con un pequeño retraso (staggered) por cada punto
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue = 1.3f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 500, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(index * 150) // Retraso escalonado para el efecto de onda
                ),
                label = "dot_scale_$index"
            )

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .scale(scale)
                    .background(color = VerdePrincipal, shape = CircleShape)
            )
        }
    }
}