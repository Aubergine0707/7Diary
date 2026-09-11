package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class WeatherType(
    val displayName: String,
    val icon: ImageVector,
    val badgeColor: Color
) {
    SUNNY("Sunny", Icons.Filled.WbSunny, Color(0xFFFFB300)),
    CLOUDY("Cloudy", Icons.Filled.Cloud, Color(0xFF78909C)),
    PARTLY_CLOUDY("Partly Cloudy", Icons.Filled.WbCloudy, Color(0xFF64B5F6)),
    RAINY("Rainy", Icons.Filled.WaterDrop, Color(0xFF42A5F5)),
    THUNDERSTORM("Thunderstorm", Icons.Filled.Thunderstorm, Color(0xFF7E57C2)),
    SNOWY("Snowy", Icons.Filled.AcUnit, Color(0xFF4DD0E1)),
    WINDY("Windy", Icons.Filled.Air, Color(0xFF26A69A));

    companion object {
        fun fromString(name: String?): WeatherType {
            return entries.find { it.name.equals(name, ignoreCase = true) } ?: SUNNY
        }
    }
}
