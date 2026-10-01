package com.tungsten.fcllibrary.component.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
fun FCLComposeTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val themeData by ThemeEngine.theme.collectAsState()
    val primary = Color(themeData?.getColor() ?: 0xFF777777.toInt())
    val secondary = Color(themeData?.getColor2() ?: 0xFF4F6367.toInt())
    val onPrimary = Color(themeData?.autoTint ?: android.graphics.Color.WHITE)
    val colorScheme = if (ThemeEngine.isNightMode(context)) {
        darkColorScheme(primary = primary, onPrimary = onPrimary, secondary = secondary)
    } else {
        lightColorScheme(primary = primary, onPrimary = onPrimary, secondary = secondary)
    }
    MaterialTheme(colorScheme = colorScheme, content = content)
}