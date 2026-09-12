package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DiaryEntry
import com.example.util.LocalAppStrings
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CalendarMonthView(
    entries: List<DiaryEntry>,
    selectedDate: Long?,
    onDateSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var calendarMonth by remember {
        mutableStateOf(Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
        })
    }

    val monthYearFormat = remember(strings.isZh) {
        if (strings.isZh) SimpleDateFormat("yyyy年M月", Locale.CHINESE)
        else SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    }
    val dayHeaders = remember(strings.isZh) {
        if (strings.isZh) listOf("日", "一", "二", "三", "四", "五", "六")
        else listOf("S", "M", "T", "W", "T", "F", "S")
    }

    // Map of day of month -> list of entries
    val entriesInCurrentMonth = remember(entries, calendarMonth) {
        val currentYear = calendarMonth.get(Calendar.YEAR)
        val currentMonth = calendarMonth.get(Calendar.MONTH)
        val map = mutableMapOf<Int, MutableList<DiaryEntry>>()

        val entryCal = Calendar.getInstance()
        entries.forEach { entry ->
            entryCal.timeInMillis = entry.dateTimestamp
            if (entryCal.get(Calendar.YEAR) == currentYear && entryCal.get(Calendar.MONTH) == currentMonth) {
                val day = entryCal.get(Calendar.DAY_OF_MONTH)
                map.getOrPut(day) { mutableListOf() }.add(entry)
            }
        }
        map
    }

    val selectedDayNumber = remember(selectedDate, calendarMonth) {
        if (selectedDate == null) null
        else {
            val c = Calendar.getInstance().apply { timeInMillis = selectedDate }
            if (c.get(Calendar.YEAR) == calendarMonth.get(Calendar.YEAR) &&
                c.get(Calendar.MONTH) == calendarMonth.get(Calendar.MONTH)
            ) {
                c.get(Calendar.DAY_OF_MONTH)
            } else null
        }
    }

    val todayCalendar = Calendar.getInstance()
    val isTodayInCurrentMonth = todayCalendar.get(Calendar.YEAR) == calendarMonth.get(Calendar.YEAR) &&
            todayCalendar.get(Calendar.MONTH) == calendarMonth.get(Calendar.MONTH)
    val todayDay = if (isTodayInCurrentMonth) todayCalendar.get(Calendar.DAY_OF_MONTH) else -1

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Month Navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        val newCal = calendarMonth.clone() as Calendar
                        newCal.add(Calendar.MONTH, -1)
                        calendarMonth = newCal
                    },
                    modifier = Modifier.testTag("btn_prev_month")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = strings.calendarPrevMonth
                    )
                }

                Text(
                    text = monthYearFormat.format(calendarMonth.time),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = {
                            calendarMonth = Calendar.getInstance().apply {
                                set(Calendar.DAY_OF_MONTH, 1)
                            }
                            onDateSelected(System.currentTimeMillis())
                        },
                        modifier = Modifier.testTag("btn_calendar_today")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Today,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(strings.today)
                    }

                    IconButton(
                        onClick = {
                            val newCal = calendarMonth.clone() as Calendar
                            newCal.add(Calendar.MONTH, 1)
                            calendarMonth = newCal
                        },
                        modifier = Modifier.testTag("btn_next_month")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = strings.calendarNextMonth
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Day name labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                dayHeaders.forEach { header ->
                    Text(
                        text = header,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Calculate calendar days grid
            val firstDayOfWeek = calendarMonth.get(Calendar.DAY_OF_WEEK) // 1=Sunday, 2=Monday, ...
            val daysInMonth = calendarMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
            val totalSlots = ((firstDayOfWeek - 1) + daysInMonth + 6) / 7 * 7

            for (week in 0 until (totalSlots / 7)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    for (dayOfWeek in 1..7) {
                        val slotIndex = week * 7 + (dayOfWeek - 1)
                        val dayNum = slotIndex - (firstDayOfWeek - 1) + 1

                        if (dayNum in 1..daysInMonth) {
                            val isSelected = dayNum == selectedDayNumber
                            val isToday = dayNum == todayDay
                            val dayEntries = entriesInCurrentMonth[dayNum] ?: emptyList()
                            val hasEntries = dayEntries.isNotEmpty()

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isSelected -> MaterialTheme.colorScheme.primary
                                            isToday -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                            else -> Color.Transparent
                                        }
                                    )
                                    .clickable {
                                        if (isSelected) {
                                            // Deselect to show all
                                            onDateSelected(null)
                                        } else {
                                            val selectedCal = calendarMonth.clone() as Calendar
                                            selectedCal.set(Calendar.DAY_OF_MONTH, dayNum)
                                            onDateSelected(selectedCal.timeInMillis)
                                        }
                                    }
                                    .testTag("calendar_day_$dayNum"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "$dayNum",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 13.sp
                                        ),
                                        color = when {
                                            isSelected -> MaterialTheme.colorScheme.onPrimary
                                            isToday -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.onSurface
                                        }
                                    )

                                    if (hasEntries) {
                                        Box(
                                            modifier = Modifier
                                                .size(5.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isSelected) MaterialTheme.colorScheme.onPrimary
                                                    else MaterialTheme.colorScheme.tertiary
                                                )
                                        )
                                    }
                                }
                            }
                        } else {
                            // Blank filler day
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )
                        }
                    }
                }
            }

            if (selectedDate != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onDateSelected(null) }) {
                        Text(strings.showAllEntries)
                    }
                }
            }
        }
    }
}
