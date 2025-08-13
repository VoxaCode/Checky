package com.voxacode.checky.core.theme 

import android.content.Context
import android.content.res.Configuration
import android.app.Activity
import android.os.Build

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

import com.materialkolor.rememberDynamicColorScheme

private fun isAboveRedVelvetCake(): Boolean = 
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

private fun defaultThemeMode(): ThemeMode = 
    if(isAboveRedVelvetCake()) ThemeMode.Dynamic
    else ThemeMode.Classic
    
private fun shouldUseDarkTheme(type: ThemeType, context: Context): Boolean = when(type) {
    is ThemeType.Dark -> true
    is ThemeType.Light -> false
    is ThemeType.System -> {
        val darkTheme = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        darkTheme == Configuration.UI_MODE_NIGHT_YES
    }
}    
    
@Composable
fun CheckyAppTheme(
    type: ThemeType = ThemeType.System,
    theme: ThemeMode = defaultThemeMode(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val darkTheme = shouldUseDarkTheme(type, context)
    
    val colorScheme = when {
        theme is ThemeMode.Dynamic && isAboveRedVelvetCake() -> {
            if(darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        
        theme is ThemeMode.Custom -> rememberDynamicColorScheme(
            seedColor = theme.seedColor,
            isDark = darkTheme
        )    
        
        else -> throw Exception(
            "theme mode isn't implemented"
        )
    }
    
    val view = LocalView.current 
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            window.statusBarColor = colorScheme.primary.toArgb()
            insetsController.isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

sealed class ThemeType() {
    object System : ThemeType()
    object Dark : ThemeType()
    object Light : ThemeType()
}

sealed class ThemeMode() {
    object Dynamic : ThemeMode()
    data class Custom(val seedColor: Color) : ThemeMode()
    companion object {
        val Classic = Custom(ClassicThemeSeed)     
    }
}