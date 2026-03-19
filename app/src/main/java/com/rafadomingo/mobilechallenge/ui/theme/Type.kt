package com.rafadomingo.mobilechallenge.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.rafadomingo.mobilechallenge.R

val MatemasieFontFamily = FontFamily(
    Font(R.font.matemasie, FontWeight.Normal),
    Font(R.font.matemasie, FontWeight.Bold)
)

val PlayfairDisplayFontFamily = FontFamily(
    Font(R.font.playfair_display, FontWeight.Normal),
    Font(R.font.playfair_display, FontWeight.Medium),
    Font(R.font.playfair_display, FontWeight.SemiBold),
    Font(R.font.playfair_display, FontWeight.Bold)
)

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = MatemasieFontFamily, fontWeight = FontWeight.Bold, fontSize = 57.sp),
    displayMedium = TextStyle(
        fontFamily = MatemasieFontFamily, fontWeight = FontWeight.Bold, fontSize = 45.sp),
    displaySmall = TextStyle(
        fontFamily = MatemasieFontFamily, fontWeight = FontWeight.Bold, fontSize = 36.sp),
    headlineLarge = TextStyle(
        fontFamily = MatemasieFontFamily, fontWeight = FontWeight.Bold, fontSize = 32.sp),
    headlineMedium = TextStyle(
        fontFamily = MatemasieFontFamily, fontWeight = FontWeight.Bold, fontSize = 28.sp),
    headlineSmall = TextStyle(
        fontFamily = MatemasieFontFamily, fontWeight = FontWeight.Bold, fontSize = 24.sp),
    titleLarge = TextStyle(
        fontFamily = MatemasieFontFamily, fontWeight = FontWeight.Bold, fontSize = 22.sp),
    titleMedium = TextStyle(
        fontFamily = MatemasieFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp),
    titleSmall = TextStyle(
        fontFamily = MatemasieFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp),
    bodyLarge = TextStyle(
        fontFamily = PlayfairDisplayFontFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(
        fontFamily = PlayfairDisplayFontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(
        fontFamily = PlayfairDisplayFontFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(
        fontFamily = PlayfairDisplayFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp),
    labelMedium = TextStyle(
        fontFamily = PlayfairDisplayFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
    labelSmall = TextStyle(
        fontFamily = PlayfairDisplayFontFamily, fontWeight = FontWeight.Medium, fontSize = 11.sp)
)
