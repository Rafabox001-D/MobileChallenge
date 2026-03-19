package com.rafadomingo.mobilechallenge.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimens(
    val paddingSmall: Dp = 4.dp,
    val paddingMedium: Dp = 8.dp,
    val paddingLarge: Dp = 16.dp,
    val paddingExtraLarge: Dp = 24.dp,
    val paddingHuge: Dp = 32.dp,
    
    val cornerRadiusSmall: Dp = 8.dp,
    val cornerRadiusMedium: Dp = 12.dp,
    val cornerRadiusLarge: Dp = 24.dp,
    val cornerRadiusExtraLarge: Dp = 28.dp,
    
    val iconSizeSmall: Dp = 24.dp,
    val iconSizeMedium: Dp = 40.dp,
    val iconSizeLarge: Dp = 70.dp,
    
    val cardElevation: Dp = 4.dp,
    val shadowElevation: Dp = 6.dp
)

val LocalDimens = compositionLocalOf { Dimens() }
