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

// ================= VIOLET / AMETHYST =================
private val VioletLight = lightColorScheme(
    primary = Color(0xFF6750A4),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFEADDFF),
    onPrimaryContainer = Color(0xFF21005D),
    secondary = Color(0xFF625B71),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE8DEF8),
    onSecondaryContainer = Color(0xFF1D192B),
    tertiary = Color(0xFF7D5260),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFD8E4),
    onTertiaryContainer = Color(0xFF31111D),
    background = Color(0xFFFEF7FF),
    onBackground = Color(0xFF1D1B20),
    surface = Color(0xFFFEF7FF),
    onSurface = Color(0xFF1D1B20),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F)
)

private val VioletDark = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    onPrimary = Color(0xFF381E72),
    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = Color(0xFFEADDFF),
    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF332D41),
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = Color(0xFFE8DEF8),
    tertiary = Color(0xFFEFB8C8),
    onTertiary = Color(0xFF492532),
    tertiaryContainer = Color(0xFF633B48),
    onTertiaryContainer = Color(0xFFFFD8E4),
    background = Color(0xFF141218),
    onBackground = Color(0xFFE6E0E9),
    surface = Color(0xFF141218),
    onSurface = Color(0xFFE6E0E9),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0)
)

// ================= OCEAN / SAPPHIRE =================
private val OceanLight = lightColorScheme(
    primary = Color(0xFF0061A4),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Color(0xFF535F70),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD7E3F7),
    onSecondaryContainer = Color(0xFF101C2B),
    tertiary = Color(0xFF00677D),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFB3EBFF),
    onTertiaryContainer = Color(0xFF001F27),
    background = Color(0xFFF8F9FF),
    onBackground = Color(0xFF191C20),
    surface = Color(0xFFF8F9FF),
    onSurface = Color(0xFF191C20),
    surfaceVariant = Color(0xFFDFE2EB),
    onSurfaceVariant = Color(0xFF42474E)
)

private val OceanDark = darkColorScheme(
    primary = Color(0xFF9ECAFF),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF00497D),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFFBBC7DB),
    onSecondary = Color(0xFF253140),
    secondaryContainer = Color(0xFF3B4858),
    onSecondaryContainer = Color(0xFFD7E3F7),
    tertiary = Color(0xFF5DD5FC),
    onTertiary = Color(0xFF003542),
    tertiaryContainer = Color(0xFF004E5F),
    onTertiaryContainer = Color(0xFFB3EBFF),
    background = Color(0xFF111418),
    onBackground = Color(0xFFE2E2E6),
    surface = Color(0xFF111418),
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = Color(0xFF42474E),
    onSurfaceVariant = Color(0xFFC3C7D0)
)

// ================= FOREST / SAGE =================
private val ForestLight = lightColorScheme(
    primary = Color(0xFF246B39),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFA7F3B3),
    onPrimaryContainer = Color(0xFF00210A),
    secondary = Color(0xFF516351),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD4E8D2),
    onSecondaryContainer = Color(0xFF0F1F11),
    tertiary = Color(0xFF39656C),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFBDEAF3),
    onTertiaryContainer = Color(0xFF001F24),
    background = Color(0xFFF7FAF2),
    onBackground = Color(0xFF191D19),
    surface = Color(0xFFF7FAF2),
    onSurface = Color(0xFF191D19),
    surfaceVariant = Color(0xFFDEE5D9),
    onSurfaceVariant = Color(0xFF424941)
)

private val ForestDark = darkColorScheme(
    primary = Color(0xFF8CD799),
    onPrimary = Color(0xFF003916),
    primaryContainer = Color(0xFF055224),
    onPrimaryContainer = Color(0xFFA7F3B3),
    secondary = Color(0xFFB8CCB7),
    onSecondary = Color(0xFF233425),
    secondaryContainer = Color(0xFF394B3A),
    onSecondaryContainer = Color(0xFFD4E8D2),
    tertiary = Color(0xFFA1CED6),
    onTertiary = Color(0xFF00363D),
    tertiaryContainer = Color(0xFF1F4D54),
    onTertiaryContainer = Color(0xFFBDEAF3),
    background = Color(0xFF101410),
    onBackground = Color(0xFFE1E4DE),
    surface = Color(0xFF101410),
    onSurface = Color(0xFFE1E4DE),
    surfaceVariant = Color(0xFF424941),
    onSurfaceVariant = Color(0xFFC2C9BD)
)

// ================= TERRACOTTA / AMBER =================
private val TerracottaLight = lightColorScheme(
    primary = Color(0xFF904A27),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDBCF),
    onPrimaryContainer = Color(0xFF381000),
    secondary = Color(0xFF77574B),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDBCF),
    onSecondaryContainer = Color(0xFF2C160D),
    tertiary = Color(0xFF6B5E2F),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF5E2A7),
    onTertiaryContainer = Color(0xFF231B00),
    background = Color(0xFFFFF8F6),
    onBackground = Color(0xFF231915),
    surface = Color(0xFFFFF8F6),
    onSurface = Color(0xFF231915),
    surfaceVariant = Color(0xFFF5DED6),
    onSurfaceVariant = Color(0xFF53433E)
)

private val TerracottaDark = darkColorScheme(
    primary = Color(0xFFFFB598),
    onPrimary = Color(0xFF552103),
    primaryContainer = Color(0xFF733514),
    onPrimaryContainer = Color(0xFFFFDBCF),
    secondary = Color(0xFFE7BDB0),
    onSecondary = Color(0xFF442A20),
    secondaryContainer = Color(0xFF5D4035),
    onSecondaryContainer = Color(0xFFFFDBCF),
    tertiary = Color(0xFFD8C68D),
    onTertiary = Color(0xFF3A3005),
    tertiaryContainer = Color(0xFF52461A),
    onTertiaryContainer = Color(0xFFF5E2A7),
    background = Color(0xFF1A120E),
    onBackground = Color(0xFFF1DFDA),
    surface = Color(0xFF1A120E),
    onSurface = Color(0xFFF1DFDA),
    surfaceVariant = Color(0xFF53433E),
    onSurfaceVariant = Color(0xFFD8C2BB)
)

enum class MaterialColorPalette(
    val id: String,
    val title: String,
    val subtitle: String,
    val previewColor: Color,
    val isDynamic: Boolean = false
) {
    NOTHING(
        id = "NOTHING",
        title = "Nothing Monochrome",
        subtitle = "Industrial stark B&W with red accent",
        previewColor = Color(0xFF1E1E1E)
    ),
    DYNAMIC(
        id = "DYNAMIC",
        title = "Dynamic Material You",
        subtitle = "Harmonizes with device wallpaper",
        previewColor = Color(0xFF6750A4),
        isDynamic = true
    ),
    VIOLET(
        id = "VIOLET",
        title = "Amethyst Violet",
        subtitle = "Classic expressive Material 3",
        previewColor = Color(0xFF6750A4)
    ),
    OCEAN(
        id = "OCEAN",
        title = "Sapphire Ocean",
        subtitle = "Serene deep blue & cyan",
        previewColor = Color(0xFF0061A4)
    ),
    FOREST(
        id = "FOREST",
        title = "Sage Forest",
        subtitle = "Calm botanical emerald & mint",
        previewColor = Color(0xFF246B39)
    ),
    TERRACOTTA(
        id = "TERRACOTTA",
        title = "Terracotta Amber",
        subtitle = "Warm earthen clay & sunset glow",
        previewColor = Color(0xFF904A27)
    );

    fun getColorScheme(isDark: Boolean): ColorScheme {
        return when (this) {
            NOTHING -> if (isDark) NothingDark else NothingLight
            VIOLET, DYNAMIC -> if (isDark) VioletDark else VioletLight
            OCEAN -> if (isDark) OceanDark else OceanLight
            FOREST -> if (isDark) ForestDark else ForestLight
            TERRACOTTA -> if (isDark) TerracottaDark else TerracottaLight
        }
    }

    companion object {
        fun fromId(id: String): MaterialColorPalette {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: NOTHING
        }
    }
}
