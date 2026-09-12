package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// ================= NOTHING OS MONOCHROME =================
val NothingRed = Color(0xFFD71920)

private val NothingLight = lightColorScheme(
    primary = Color(0xFF161616),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF2C2C2C),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFF4A4A4A),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE5E5E5),
    onSecondaryContainer = Color(0xFF161616),
    tertiary = NothingRed,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDAD6),
    onTertiaryContainer = Color(0xFF410003),
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF121212),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF121212),
    surfaceVariant = Color(0xFFEBEBEB),
    onSurfaceVariant = Color(0xFF424242),
    surfaceContainer = Color(0xFFF2F2F2),
    surfaceContainerHigh = Color(0xFFE8E8E8),
    surfaceContainerHighest = Color(0xFFDDDDDD),
    outline = Color(0xFF757575),
    outlineVariant = Color(0xFFCCCCCC)
)

private val NothingDark = darkColorScheme(
    primary = Color(0xFFF5F5F5),
    onPrimary = Color(0xFF121212),
    primaryContainer = Color(0xFF2E2E2E),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFFBDBDBD),
    onSecondary = Color(0xFF1E1E1E),
    secondaryContainer = Color(0xFF333333),
    onSecondaryContainer = Color(0xFFEEEEEE),
    tertiary = NothingRed,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFF93000A),
    onTertiaryContainer = Color(0xFFFFDAD6),
    background = Color(0xFF0C0C0C),
    onBackground = Color(0xFFE5E5E5),
    surface = Color(0xFF141414),
    onSurface = Color(0xFFF0F0F0),
    surfaceVariant = Color(0xFF242424),
    onSurfaceVariant = Color(0xFFB0B0B0),
    surfaceContainer = Color(0xFF1A1A1A),
    surfaceContainerHigh = Color(0xFF222222),
    surfaceContainerHighest = Color(0xFF2A2A2A),
    outline = Color(0xFF888888),
    outlineVariant = Color(0xFF444444)
)

// ================= MATERIAL FALLBACK (WHEN DYNAMIC IS OFF/UNSUPPORTED) =================
private val MaterialDefaultLight = lightColorScheme(
    primary = Color(0xFF0061A4),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Color(0xFF535F70),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD7E3F7),
    onSecondaryContainer = Color(0xFF101C2B),
    tertiary = Color(0xFF6750A4),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFEADDFF),
    onTertiaryContainer = Color(0xFF21005D),
    background = Color(0xFFFDFBFF),
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFFDFBFF),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFDFE2EB),
    onSurfaceVariant = Color(0xFF42474E),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF7F9FD),
    surfaceContainer = Color(0xFFF1F4F9),
    surfaceContainerHigh = Color(0xFFEBEEF3),
    surfaceContainerHighest = Color(0xFFE5E8ED),
    outline = Color(0xFF73777F),
    outlineVariant = Color(0xFFC3C7D0)
)

private val MaterialDefaultDark = darkColorScheme(
    primary = Color(0xFF9ECAFF),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF00497D),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFFBBC7DB),
    onSecondary = Color(0xFF253140),
    secondaryContainer = Color(0xFF3B4858),
    onSecondaryContainer = Color(0xFFD7E3F7),
    tertiary = Color(0xFFD0BCFF),
    onTertiary = Color(0xFF381E72),
    tertiaryContainer = Color(0xFF4F378B),
    onTertiaryContainer = Color(0xFFEADDFF),
    background = Color(0xFF111418),
    onBackground = Color(0xFFE2E2E6),
    surface = Color(0xFF111418),
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = Color(0xFF42474E),
    onSurfaceVariant = Color(0xFFC3C7D0),
    surfaceContainerLowest = Color(0xFF0C0F12),
    surfaceContainerLow = Color(0xFF191C20),
    surfaceContainer = Color(0xFF1D2024),
    surfaceContainerHigh = Color(0xFF282A2E),
    surfaceContainerHighest = Color(0xFF333539),
    outline = Color(0xFF8D9199),
    outlineVariant = Color(0xFF42474E)
)

enum class MaterialColorPalette(
    val id: String,
    val title: String,
    val subtitle: String,
    val previewColor: Color,
    val isDynamic: Boolean = false
) {
    DYNAMIC(
        id = "DYNAMIC",
        title = "Dynamic Material You",
        subtitle = "Harmonizes with device wallpaper & M3 dynamic tokens",
        previewColor = Color(0xFF0061A4),
        isDynamic = true
    ),
    NOTHING(
        id = "NOTHING",
        title = "Nothing",
        subtitle = "Industrial stark monochrome dot aesthetic",
        previewColor = Color(0xFF1E1E1E)
    );

    fun getColorScheme(isDark: Boolean): ColorScheme {
        return when (this) {
            NOTHING -> if (isDark) NothingDark else NothingLight
            DYNAMIC -> if (isDark) MaterialDefaultDark else MaterialDefaultLight
        }
    }

    companion object {
        fun fromId(id: String): MaterialColorPalette {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: DYNAMIC
        }
    }
}
