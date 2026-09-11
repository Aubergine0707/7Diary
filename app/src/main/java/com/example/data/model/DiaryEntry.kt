package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_entries")
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val dateTimestamp: Long = System.currentTimeMillis(),
    val weather: String = WeatherType.SUNNY.name,
    val mood: String = MoodType.HAPPY.name,
    val photoPaths: String = "", // Stored as comma or newline-separated paths
    val audioPath: String? = null,
    val audioDurationSeconds: Int = 0,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val photoList: List<String>
        get() = if (photoPaths.isBlank()) emptyList() else photoPaths.split("|||").filter { it.isNotBlank() }

    val weatherType: WeatherType
        get() = WeatherType.fromString(weather)

    val moodType: MoodType
        get() = MoodType.fromString(mood)

    companion object {
        fun createPhotoPathsString(photos: List<String>): String {
            return photos.joinToString("|||")
        }
    }
}
