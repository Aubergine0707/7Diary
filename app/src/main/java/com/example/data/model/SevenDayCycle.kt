package com.example.data.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Represents a 7-day recording unit in 7Diary.
 * Each cycle spans Monday (Day 1) to Sunday (Day 7).
 */
data class SevenDayCycle(
    val cycleKey: String, // e.g., "2026-W37"
    val weekNumber: Int,
    val year: Int,
    val startMillis: Long,
    val endMillis: Long,
    val displayTitle: String,
    val dateRangeLabel: String,
    val isCurrentCycle: Boolean,
    val days: List<CycleDayItem>,
    val entries: List<DiaryEntry>,
    val recordedDaysCount: Int,
    val reflectionText: String = ""
) {
    val completionPercentage: Float
        get() = (recordedDaysCount / 7f).coerceIn(0f, 1f)

    val isComplete: Boolean
        get() = recordedDaysCount >= 7

    val photosCount: Int
        get() = entries.sumOf { it.photoList.size }

    val audioNotesCount: Int
        get() = entries.count { !it.audioPath.isNullOrBlank() }

    val dominantMood: MoodType?
        get() {
            if (entries.isEmpty()) return null
            return entries.map { it.moodType }
                .groupingBy { it }
                .eachCount()
                .maxByOrNull { it.value }
                ?.key
        }
}

data class CycleDayItem(
    val dayNumber: Int, // 1 (Mon) to 7 (Sun)
    val dayNameShort: String, // "Mon", "Tue", ...
    val dayNameFull: String, // "Monday", ...
    val dateMillis: Long,
    val formattedDate: String, // "Sep 7"
    val isToday: Boolean,
    val isFuture: Boolean,
    val entries: List<DiaryEntry>
) {
    val hasEntry: Boolean
        get() = entries.isNotEmpty()

    val primaryMood: MoodType?
        get() = entries.firstOrNull()?.moodType

    val primaryWeather: WeatherType?
        get() = entries.firstOrNull()?.weatherType
}

object SevenDayCycleHelper {
    private val rangeDateFormat = SimpleDateFormat("MMM d", Locale.US)
    private val dayDateFormat = SimpleDateFormat("MMM d", Locale.US)
    private val dayNameFormat = SimpleDateFormat("EEE", Locale.US)
    private val fullDayNameFormat = SimpleDateFormat("EEEE", Locale.US)

    /**
     * Calculates the 7-day cycle for a given week offset relative to today (0 = current cycle, -1 = previous, etc.)
     */
    fun getCycleForWeekOffset(
        weekOffset: Int,
        allEntries: List<DiaryEntry>,
        reflectionText: String = ""
    ): SevenDayCycle {
        val calendar = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            add(Calendar.WEEK_OF_YEAR, weekOffset)
        }
        return buildCycleFromCalendar(calendar, allEntries, reflectionText)
    }

    /**
     * Calculates the 7-day cycle for a specific timestamp
     */
    fun getCycleForTimestamp(
        timestamp: Long,
        allEntries: List<DiaryEntry>,
        reflectionText: String = ""
    ): SevenDayCycle {
        val calendar = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            timeInMillis = timestamp
        }
        return buildCycleFromCalendar(calendar, allEntries, reflectionText)
    }

    /**
     * Returns day index in the 7-day cycle (1 = Monday, ..., 7 = Sunday)
     */
    fun getDayNumberInCycle(timestamp: Long): Int {
        val cal = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            timeInMillis = timestamp
        }
        return when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            Calendar.SUNDAY -> 7
            else -> 1
        }
    }

    /**
     * Returns day of cycle label e.g., "Day 5 of 7 • Friday"
     */
    fun getDayOfCycleLabel(timestamp: Long): String {
        val dayNum = getDayNumberInCycle(timestamp)
        val dayName = fullDayNameFormat.format(Date(timestamp))
        return "Day $dayNum of 7 • $dayName"
    }

    /**
     * Returns cycle key for a timestamp (e.g., "2026-W37")
     */
    fun getCycleKey(timestamp: Long): String {
        val cal = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            timeInMillis = timestamp
        }
        val year = cal.get(Calendar.YEAR)
        val week = cal.get(Calendar.WEEK_OF_YEAR)
        return String.format(Locale.US, "%d-W%02d", year, week)
    }

    private fun buildCycleFromCalendar(
        baseCal: Calendar,
        allEntries: List<DiaryEntry>,
        reflectionText: String
    ): SevenDayCycle {
        val todayCal = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
        }
        val todayStartMillis = getDayStartMillis(todayCal.timeInMillis)

        // Set to Monday 00:00:00 of the week
        val startCal = (baseCal.clone() as Calendar).apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startMillis = startCal.timeInMillis

        // Set to Sunday 23:59:59 of the week
        val endCal = (baseCal.clone() as Calendar).apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val endMillis = endCal.timeInMillis

        val weekNum = baseCal.get(Calendar.WEEK_OF_YEAR)
        val year = baseCal.get(Calendar.YEAR)
        val cycleKey = String.format(Locale.US, "%d-W%02d", year, weekNum)

        val isCurrent = todayCal.timeInMillis in startMillis..endMillis

        // Gather entries belonging to this 7-day cycle
        val cycleEntries = allEntries.filter { it.dateTimestamp in startMillis..endMillis }
            .sortedBy { it.dateTimestamp }

        // Build the 7 individual day slots
        val days = (0 until 7).map { dayOffset ->
            val dayCal = (startCal.clone() as Calendar).apply {
                add(Calendar.DAY_OF_MONTH, dayOffset)
            }
            val dayStart = getDayStartMillis(dayCal.timeInMillis)
            val dayEnd = dayStart + (24 * 60 * 60 * 1000) - 1

            val dayEntries = cycleEntries.filter { it.dateTimestamp in dayStart..dayEnd }
            val isToday = dayStart == todayStartMillis
            val isFuture = dayStart > todayStartMillis

            CycleDayItem(
                dayNumber = dayOffset + 1,
                dayNameShort = dayNameFormat.format(dayCal.time),
                dayNameFull = fullDayNameFormat.format(dayCal.time),
                dateMillis = dayCal.timeInMillis,
                formattedDate = dayDateFormat.format(dayCal.time),
                isToday = isToday,
                isFuture = isFuture,
                entries = dayEntries
            )
        }

        val recordedDays = days.count { it.hasEntry }
        val dateRangeLabel = "${rangeDateFormat.format(Date(startMillis))} – ${rangeDateFormat.format(Date(endMillis))}"
        val displayTitle = "Cycle #$weekNum • $dateRangeLabel, $year"

        return SevenDayCycle(
            cycleKey = cycleKey,
            weekNumber = weekNum,
            year = year,
            startMillis = startMillis,
            endMillis = endMillis,
            displayTitle = displayTitle,
            dateRangeLabel = dateRangeLabel,
            isCurrentCycle = isCurrent,
            days = days,
            entries = cycleEntries,
            recordedDaysCount = recordedDays,
            reflectionText = reflectionText
        )
    }

    private fun getDayStartMillis(timestamp: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}
