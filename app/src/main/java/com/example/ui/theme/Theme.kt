package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val EnclaveDarkColorScheme = darkColorScheme(
    primary = LuciferinCyan,
    onPrimary = ObsidianNight,
    primaryContainer = ConsoleSurfaceVariant,
    onPrimaryContainer = TextPrimary,
    secondary = ForgeAmber,
    onSecondary = ObsidianNight,
    secondaryContainer = ConsoleSurfaceVariant,
    onSecondaryContainer = ForgeAmber,
    tertiary = SimulacrumViolet,
    onTertiary = TextPrimary,
    background = ObsidianNight,
    onBackground = TextPrimary,
    surface = ConsoleGlass,
    onSurface = TextPrimary,
    surfaceVariant = ConsoleSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = VexCrimson,
    onError = ObsidianNight,
    outline = BrassGold
)

@Composable
fun EnclaveTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EnclaveDarkColorScheme,
        typography = Typography,
        content = content
    )
}
