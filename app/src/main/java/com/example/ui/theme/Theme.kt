package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MrKolapieColorScheme = darkColorScheme(
  primary = MetallicTeal,
  onPrimary = DeepSpaceNavy,
  primaryContainer = MetallicTealDark,
  onPrimaryContainer = MetallicTealBright,
  secondary = BrushedSilver,
  onSecondary = DeepSpaceNavy,
  secondaryContainer = NavyCard,
  onSecondaryContainer = BrushedSilverLight,
  tertiary = MetallicTealBright,
  onTertiary = DeepSpaceNavy,
  background = DeepSpaceNavy,
  onBackground = TextPrimary,
  surface = NavySurface,
  onSurface = TextPrimary,
  surfaceVariant = NavyCard,
  onSurfaceVariant = TextSecondary,
  outline = NavyBorder,
  outlineVariant = BrushedSilverDark
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = MrKolapieColorScheme

  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      window.statusBarColor = DeepSpaceNavy.toArgb()
      window.navigationBarColor = DeepSpaceNavy.toArgb()
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
      WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

