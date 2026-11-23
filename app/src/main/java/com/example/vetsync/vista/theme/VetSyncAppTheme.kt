package com.example.vetsync.vista.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VetSyncLightColorScheme = lightColorScheme(
    primary = VerdeVetSync,
    onPrimary = Color.White,

    primaryContainer = VerdeClaroIcono,
    onPrimaryContainer = TextoOscuro,

    secondary = VerdeOscuro,
    onSecondary = Color.White,

    secondaryContainer = FondoAccesos,
    onSecondaryContainer = TextoOscuro,

    tertiary = Color(0xFF8B5B45),
    onTertiary = Color.White,

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

private val VetSyncDarkColorScheme = darkColorScheme(
    primary = Color(0xFFA9C9AD),
    onPrimary = Color(0xFF19351F),

    primaryContainer = Color(0xFF34543B),
    onPrimaryContainer = Color(0xFFE1F0E3),

    secondary = Color(0xFFC1D2C1),
    onSecondary = Color(0xFF29392D),

    secondaryContainer = Color(0xFF344238),
    onSecondaryContainer = Color(0xFFE3EADF),

    tertiary = Color(0xFFE6B69F),
    onTertiary = Color(0xFF452B20),

    background = Color(0xFF111713),
    onBackground = Color(0xFFE5EBE5),

    surface = Color(0xFF1A211C),
    onSurface = Color(0xFFE5EBE5),

    surfaceVariant = Color(0xFF303A33),
    onSurfaceVariant = Color(0xFFC0CBC1),

    outline = Color(0xFF7D8C7E),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),

    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    inverseSurface = Color(0xFFE5EBE5),
    inverseOnSurface = Color(0xFF2E332F),
    inversePrimary = VerdeVetSync
)

@Composable
fun VetSyncAppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        VetSyncDarkColorScheme
    } else {
        VetSyncLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}