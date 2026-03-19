package com.rafadomingo.mobilechallenge.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = NeonGreenAccent,
    onPrimary = DarkPurpleBackground,
    secondary = DarkPurplePrimary,
    background = DarkPurpleBackground,
    surface = DarkSurface,
    onBackground = DarkPurplePrimary,
    onSurface = DarkPurplePrimary
)

private val LightColorScheme = lightColorScheme(
    primary = OrangeAccent,
    onPrimary = LightPurpleBackground,
    secondary = LightPurplePrimary,
    background = LightPurpleBackground,
    surface = LightSurface,
    onBackground = LightPurplePrimary,
    onSurface = LightPurplePrimary
)

@Composable
fun MobileChallengeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dimens: Dimens = Dimens(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context.findActivity()
            if (activity != null) {
                val window = activity.window
                window.statusBarColor = Color.Transparent.toArgb()
                window.navigationBarColor = Color.Transparent.toArgb()
                
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalDimens provides dimens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
