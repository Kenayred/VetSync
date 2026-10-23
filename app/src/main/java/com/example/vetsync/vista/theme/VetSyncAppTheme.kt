package com.example.vetsync.vista.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VetSyncColorScheme = lightColorScheme(
    primary = VerdeVetSync,
    onPrimary = Color.White,

    primaryContainer = VerdeClaroIcono,
    onPrimaryContainer = TextoOscuro,

    secondary = VerdeOscuro,
    onSecondary = Color.White,

    secondaryContainer = FondoAccesos,
    onSecondaryContainer = TextoOscuro,

    background = FondoCrema,
    onBackground = TextoOscuro,

    surface = Color.White,
    onSurface = TextoOscuro,

    surfaceVariant = FondoInputMascota,
    onSurfaceVariant = GrisTextoSecundario,

    outline = BordeInputMascota,

    error = RojoAlerta,
    onError = Color.White
)

@Composable
fun VetSyncAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = VetSyncColorScheme,
        content = content
    )
}