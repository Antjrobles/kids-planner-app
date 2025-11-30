/**
 * Theme - Tema principal de la app (Material 3)
 * Configuro el tema global aplicando la paleta de colores, tipografía y esquema de colores.
 * El Composable KidsPlannerTheme envuelve toda la UI para aplicar estilos consistentes.
 */
package com.antjrobles.kidsplanner.tema

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = KidsPlannerColors.Primary,
    secondary = KidsPlannerColors.Accent,
    tertiary = KidsPlannerColors.Accent,
    background = KidsPlannerColors.Background,
    surface = KidsPlannerColors.Background,
    onPrimary = KidsPlannerColors.White,
    onSecondary = KidsPlannerColors.TextPrimary,
    onTertiary = KidsPlannerColors.TextPrimary,
    onBackground = KidsPlannerColors.TextPrimary,
    onSurface = KidsPlannerColors.TextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = KidsPlannerColors.Primary,
    secondary = KidsPlannerColors.Accent,
    tertiary = KidsPlannerColors.Accent,
    background = KidsPlannerColors.Background,
    surface = KidsPlannerColors.Background,
    onPrimary = KidsPlannerColors.White,
    onSecondary = KidsPlannerColors.TextPrimary,
    onTertiary = KidsPlannerColors.TextPrimary,
    onBackground = KidsPlannerColors.TextPrimary,
    onSurface = KidsPlannerColors.TextPrimary
)

@Composable
fun KidsPlannerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}