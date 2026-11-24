package co.gov.tulua.espaciopublico.core.designsystem

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// ─── Brand palette ───────────────────────────────────────────────────────────
val Green900 = Color(0xFF0F5F3A)          // Primary dark green
val GreenContainer = Color(0xFFE8F3EA)    // Primary container / soft bg
val AppBackground = Color(0xFFF1FAF2)     // App background

val Amber500 = Color(0xFFF5B14C)          // Accent / warning
val AmberContainer = Color(0xFFFCEBC8)    // Amber background

val Red700 = Color(0xFFC62828)            // Danger
val RedContainer = Color(0xFFFDE3E3)      // Danger background

val TextPrimary = Color(0xFF1B1B1B)       // Text primary
val TextSecondary = Color(0xFF5F6B63)     // Text secondary

val CardBackground = Color.White          // Card background
val CardBorder = Color(0xFFE1EBE3)        // Card border

// ─── Neutral scale ───────────────────────────────────────────────────────────
val Gray50  = Color(0xFFFAFAFA)
val Gray100 = Color(0xFFF5F5F5)
val Gray200 = Color(0xFFEEEEEE)
val Gray400 = Color(0xFFBDBDBD)
val Gray600 = Color(0xFF757575)
val Gray800 = Color(0xFF424242)
val Gray900 = Color(0xFF212121)

// ─── Dark theme surface palette ──────────────────────────────────────────────
val DarkSurface = Color(0xFF1A1C1E)
val DarkSurfaceVariant = Color(0xFF252A27)
val DarkOnSurface = Color(0xFFE2E3DE)
val DarkBackground = Color(0xFF111411)

// ─── Light color scheme ──────────────────────────────────────────────────────
val LightColorScheme = lightColorScheme(
    primary = Green900,
    onPrimary = Color.White,
    primaryContainer = GreenContainer,
    onPrimaryContainer = Color(0xFF002112),
    secondary = Color(0xFF4E6355),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD0E8D6),
    onSecondaryContainer = Color(0xFF0B1F14),
    tertiary = Amber500,
    onTertiary = Color.White,
    tertiaryContainer = AmberContainer,
    onTertiaryContainer = Color(0xFF291800),
    error = Red700,
    onError = Color.White,
    errorContainer = RedContainer,
    onErrorContainer = Color(0xFF410002),
    background = AppBackground,
    onBackground = TextPrimary,
    surface = CardBackground,
    onSurface = TextPrimary,
    surfaceVariant = GreenContainer,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    outlineVariant = Color(0xFFC8D8CA),
    inverseSurface = Gray900,
    inverseOnSurface = Gray100,
    inversePrimary = Color(0xFF6DDBAA),
)

// ─── Dark color scheme ───────────────────────────────────────────────────────
val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6DDBAA),
    onPrimary = Color(0xFF003921),
    primaryContainer = Color(0xFF00522E),
    onPrimaryContainer = GreenContainer,
    secondary = Color(0xFFB4CCBA),
    onSecondary = Color(0xFF203528),
    secondaryContainer = Color(0xFF364B3D),
    onSecondaryContainer = Color(0xFFD0E8D6),
    tertiary = Color(0xFFFFB95A),
    onTertiary = Color(0xFF452B00),
    tertiaryContainer = Color(0xFF623F00),
    onTertiaryContainer = AmberContainer,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFBDC9BF),
    outline = Color(0xFF879380),
    outlineVariant = Color(0xFF3F4E45),
    inverseSurface = Color(0xFFE2E3DE),
    inverseOnSurface = Color(0xFF2F312E),
    inversePrimary = Green900,
)
