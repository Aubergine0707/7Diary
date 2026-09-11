package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class MoodType(
    val displayName: String,
    val emoji: String,
    val badgeColor: Color
) {
    JOYFUL("Joyful", "😄", Color(0xFFFFB74D)),
    HAPPY("Happy", "😊", Color(0xFF81C784)),
    CALM("Calm", "😌", Color(0xFF4DB6AC)),
    EXCITED("Excited", "🤩", Color(0xFFFF8A65)),
    NEUTRAL("Neutral", "😐", Color(0xFF90A4AE)),
    TIRED("Tired", "🥱", Color(0xFFBA68C8)),
    SAD("Sad", "😢", Color(0xFF64B5F6)),
    ANGRY("Angry", "😠", Color(0xFFE57373));

    companion object {
        fun fromString(name: String?): MoodType {
            return entries.find { it.name.equals(name, ignoreCase = true) } ?: HAPPY
        }
    }
}
