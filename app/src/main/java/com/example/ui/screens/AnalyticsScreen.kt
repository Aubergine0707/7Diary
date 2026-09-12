package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.DiaryEntry
import com.example.data.model.MoodType
import com.example.data.model.WeatherType
import com.example.ui.components.DiaryCard
import com.example.util.LocalAppStrings
import com.example.util.getLocalizedName
import com.example.viewmodel.AnalyticsTimeRange
import com.example.viewmodel.DiaryViewModel
import com.example.viewmodel.GalleryPhotoItem
import com.example.viewmodel.MoodStat
import com.example.viewmodel.WeatherStat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class AnalyticsSubTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    STATS("Insights", Icons.Default.BarChart),
    GALLERY("Gallery", Icons.Default.PhotoLibrary),
    ONE_YEAR_AGO("On This Day", Icons.Default.History)
}

@Composable
fun AnalyticsScreen(
    viewModel: DiaryViewModel,
    onNavigateDetail: (DiaryEntry) -> Unit,
    onNewEntryForDate: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val allEntries by viewModel.allEntries.collectAsStateWithLifecycle()
    val timeRange by viewModel.analyticsTimeRange.collectAsStateWithLifecycle()
    val referenceDateMillis by viewModel.oneYearAgoReferenceDate.collectAsStateWithLifecycle()

    var selectedSubTab by remember { mutableStateOf(AnalyticsSubTab.STATS) }
    var previewPhotoItem by remember { mutableStateOf<GalleryPhotoItem?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Analytics Sub Navigation Tabs
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnalyticsSubTab.entries.forEach { tab ->
                    val isSelected = selectedSubTab == tab
                    val tabTitle = when (tab) {
                        AnalyticsSubTab.STATS -> strings.analyticsTabStats
                        AnalyticsSubTab.GALLERY -> strings.analyticsTabGallery
                        AnalyticsSubTab.ONE_YEAR_AGO -> strings.analyticsTabOneYearAgo
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
                        border = if (isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedSubTab = tab }
                            .testTag("tab_analytics_${tab.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tabTitle,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Sub Tab Content
        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            when (selectedSubTab) {
                AnalyticsSubTab.STATS -> {
                    StatsSection(
                        allEntries = allEntries,
                        timeRange = timeRange,
                        onSelectTimeRange = { viewModel.setAnalyticsTimeRange(it) },
                        viewModel = viewModel,
                        onNavigateDetail = onNavigateDetail
                    )
                }

                AnalyticsSubTab.GALLERY -> {
                    PhotoGallerySection(
                        allEntries = allEntries,
                        viewModel = viewModel,
                        onPhotoClick = { previewPhotoItem = it },
                        onNewEntry = { onNewEntryForDate(System.currentTimeMillis()) }
                    )
                }

                AnalyticsSubTab.ONE_YEAR_AGO -> {
                    OneYearAgoSection(
                        allEntries = allEntries,
                        referenceDateMillis = referenceDateMillis,
                        viewModel = viewModel,
                        onNavigateDetail = onNavigateDetail,
                        onNewEntryForDate = onNewEntryForDate,
                        onChangeReferenceDate = { viewModel.setOneYearAgoReferenceDate(it) }
                    )
                }
            }
        }
    }

    // Photo Preview Dialog
    previewPhotoItem?.let { item ->
        PhotoViewerDialog(
            item = item,
            onDismiss = { previewPhotoItem = null },
            onOpenEntry = {
                previewPhotoItem = null
                onNavigateDetail(item.entry)
            }
        )
    }
}

// ==================== 1. STATS SECTION (MOOD & WEATHER) ====================
@Composable
private fun StatsSection(
    allEntries: List<DiaryEntry>,
    timeRange: AnalyticsTimeRange,
    onSelectTimeRange: (AnalyticsTimeRange) -> Unit,
    viewModel: DiaryViewModel,
    onNavigateDetail: (DiaryEntry) -> Unit
) {
    val strings = LocalAppStrings.current
    val filteredEntries = remember(allEntries, timeRange) {
        viewModel.filterEntriesByTimeRange(allEntries, timeRange)
    }
    val moodStats = remember(filteredEntries) {
        viewModel.computeMoodStats(filteredEntries)
    }
    val weatherStats = remember(filteredEntries) {
        viewModel.computeWeatherStats(filteredEntries)
    }

    var selectedMoodFilter by remember { mutableStateOf<MoodType?>(null) }
    var selectedWeatherFilter by remember { mutableStateOf<WeatherType?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("analytics_stats_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Time Range Filter Bar
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.analyticsTimeRangeLabel,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = String.format(strings.analyticsTotalEntriesCount, filteredEntries.size),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AnalyticsTimeRange.entries.forEach { range ->
                            val isSelected = timeRange == range
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        onSelectTimeRange(range)
                                        selectedMoodFilter = null
                                        selectedWeatherFilter = null
                                    }
                            ) {
                                Text(
                                    text = range.getLocalizedName(strings.isZh),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (filteredEntries.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = strings.analyticsNoEntriesPeriod,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = strings.analyticsNoEntriesPeriodDesc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            // ==================== 2. MOOD STATISTICS ====================
            item {
                MoodStatisticsCard(
                    moodStats = moodStats,
                    totalEntries = filteredEntries.size,
                    selectedMoodFilter = selectedMoodFilter,
                    onSelectMoodFilter = {
                        selectedMoodFilter = if (selectedMoodFilter == it) null else it
                        selectedWeatherFilter = null
                    }
                )
            }

            // ==================== 3. WEATHER STATISTICS ====================
            item {
                WeatherStatisticsCard(
                    weatherStats = weatherStats,
                    totalEntries = filteredEntries.size,
                    selectedWeatherFilter = selectedWeatherFilter,
                    onSelectWeatherFilter = {
                        selectedWeatherFilter = if (selectedWeatherFilter == it) null else it
                        selectedMoodFilter = null
                    }
                )
            }

            // Optional entries matching selected mood or weather filter
            val displayedEntries = when {
                selectedMoodFilter != null -> filteredEntries.filter { it.moodType == selectedMoodFilter }
                selectedWeatherFilter != null -> filteredEntries.filter { it.weatherType == selectedWeatherFilter }
                else -> emptyList()
            }

            if (displayedEntries.isNotEmpty()) {
                item {
                    Text(
                        text = when {
                            selectedMoodFilter != null -> String.format(strings.analyticsEntriesWithMood, selectedMoodFilter?.getLocalizedName(strings.isZh) ?: "", displayedEntries.size)
                            selectedWeatherFilter != null -> String.format(strings.analyticsEntriesWithWeather, selectedWeatherFilter?.getLocalizedName(strings.isZh) ?: "", displayedEntries.size)
                            else -> ""
                        },
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(displayedEntries, key = { it.id }) { entry ->
                    DiaryCard(
                        entry = entry,
                        onClick = { onNavigateDetail(entry) },
                        onToggleFavorite = { viewModel.toggleFavorite(entry) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun MoodStatisticsCard(
    moodStats: List<MoodStat>,
    totalEntries: Int,
    selectedMoodFilter: MoodType?,
    onSelectMoodFilter: (MoodType) -> Unit
) {
    val strings = LocalAppStrings.current
    val dominantMood = moodStats.firstOrNull()

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_mood_statistics")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mood,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.analyticsMoodTitle,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = strings.analyticsMoodDesc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (dominantMood != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = dominantMood.mood.badgeColor.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, dominantMood.mood.badgeColor.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = dominantMood.mood.emoji, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format(strings.analyticsPrimaryMood, dominantMood.mood.getLocalizedName(strings.isZh)),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Proportional Multi-Segment Horizontal Bar
            MultiSegmentProportionalBar(
                segments = moodStats.map {
                    ProportionalSegment(
                        label = it.mood.getLocalizedName(strings.isZh),
                        percentage = it.percentage,
                        color = it.mood.badgeColor
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Detailed Mood Breakdown Chips/Rows
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                moodStats.forEach { stat ->
                    val isSelected = selectedMoodFilter == stat.mood
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) stat.mood.badgeColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                        border = if (isSelected) BorderStroke(1.5.dp, stat.mood.badgeColor) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectMoodFilter(stat.mood) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = stat.mood.emoji, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = stat.mood.getLocalizedName(strings.isZh),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.width(90.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            // Custom proportional progress track
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction = (stat.percentage / 100f).coerceIn(0f, 1f))
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(stat.mood.badgeColor)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = String.format(Locale.ENGLISH, "%.1f%%", stat.percentage),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = String.format(strings.timelineWordsCount, stat.count).replace("字", "篇").replace("words", "entries"),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherStatisticsCard(
    weatherStats: List<WeatherStat>,
    totalEntries: Int,
    selectedWeatherFilter: WeatherType?,
    onSelectWeatherFilter: (WeatherType) -> Unit
) {
    val strings = LocalAppStrings.current
    val dominantWeather = weatherStats.firstOrNull()

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_weather_statistics")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.analyticsWeatherTitle,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = strings.analyticsWeatherDesc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (dominantWeather != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = dominantWeather.weather.badgeColor.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, dominantWeather.weather.badgeColor.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = dominantWeather.weather.icon,
                                contentDescription = null,
                                tint = dominantWeather.weather.badgeColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format(strings.analyticsPrimaryWeather, dominantWeather.weather.getLocalizedName(strings.isZh)),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Multi Segment Proportional Bar
            MultiSegmentProportionalBar(
                segments = weatherStats.map {
                    ProportionalSegment(
                        label = it.weather.getLocalizedName(strings.isZh),
                        percentage = it.percentage,
                        color = it.weather.badgeColor
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Detailed Weather Breakdown Rows
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                weatherStats.forEach { stat ->
                    val isSelected = selectedWeatherFilter == stat.weather
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) stat.weather.badgeColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                        border = if (isSelected) BorderStroke(1.5.dp, stat.weather.badgeColor) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectWeatherFilter(stat.weather) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = stat.weather.icon,
                                contentDescription = null,
                                tint = stat.weather.badgeColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = stat.weather.getLocalizedName(strings.isZh),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.width(90.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            // Progress Bar
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction = (stat.percentage / 100f).coerceIn(0f, 1f))
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(stat.weather.badgeColor)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = String.format(Locale.ENGLISH, "%.1f%%", stat.percentage),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = String.format(strings.timelineWordsCount, stat.count).replace("字", "篇").replace("words", "entries"),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class ProportionalSegment(
    val label: String,
    val percentage: Float,
    val color: Color
)

@Composable
private fun MultiSegmentProportionalBar(
    segments: List<ProportionalSegment>,
    modifier: Modifier = Modifier
) {
    if (segments.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        // Continuous Stacked Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                segments.forEach { seg ->
                    if (seg.percentage > 0f) {
                        Box(
                            modifier = Modifier
                                .weight(seg.percentage)
                                .height(14.dp)
                                .background(seg.color)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Legend chips row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            segments.take(5).forEach { seg ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(seg.color)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${seg.label} ${String.format(Locale.ENGLISH, "%.0f%%", seg.percentage)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ==================== 2. PHOTO GALLERY SECTION ====================
@Composable
private fun PhotoGallerySection(
    allEntries: List<DiaryEntry>,
    viewModel: DiaryViewModel,
    onPhotoClick: (GalleryPhotoItem) -> Unit,
    onNewEntry: () -> Unit
) {
    val strings = LocalAppStrings.current
    val allPhotos = remember(allEntries) {
        viewModel.extractGalleryPhotos(allEntries)
    }

    var selectedFilter by remember { mutableIntStateOf(0) }
    val filters = listOf(
        String.format(strings.analyticsFilterAllPhotos, allPhotos.size),
        strings.analyticsFilterPast7Days,
        strings.analyticsFilterPast30Days
    )

    val filteredPhotos = remember(allPhotos, selectedFilter) {
        val now = System.currentTimeMillis()
        when (selectedFilter) {
            1 -> allPhotos.filter { it.entry.dateTimestamp >= now - 7L * 86400000L }
            2 -> allPhotos.filter { it.entry.dateTimestamp >= now - 30L * 86400000L }
            else -> allPhotos
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("analytics_photo_gallery")
    ) {
        // Gallery Header Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = strings.analyticsPhotoGallery,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = strings.analyticsPhotoSubtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = String.format(strings.analyticsPhotoCount, allPhotos.size),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Filter chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filters.forEachIndexed { index, label ->
                        val isSelected = selectedFilter == index
                        ElevatedFilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = index },
                            label = { Text(label) },
                            colors = FilterChipDefaults.elevatedFilterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }

        if (filteredPhotos.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (allPhotos.isEmpty()) strings.analyticsNoPhotosInGallery else strings.analyticsNoPhotosInPeriod,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (allPhotos.isEmpty()) strings.analyticsNoPhotosDesc else strings.analyticsNoPhotosPeriodDesc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(onClick = onNewEntry) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.analyticsWritePhotoEntry)
                    }
                }
            }
        } else {
            // 3-Column Photo Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredPhotos, key = { "${it.entry.id}_${it.photoPath}" }) { item ->
                    GalleryPhotoThumbnail(
                        item = item,
                        onClick = { onPhotoClick(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GalleryPhotoThumbnail(
    item: GalleryPhotoItem,
    onClick: () -> Unit
) {
    val strings = LocalAppStrings.current
    val dateFormat = remember(strings.isZh) {
        if (strings.isZh) SimpleDateFormat("M月d日", Locale.CHINA)
        else SimpleDateFormat("MMM d", Locale.ENGLISH)
    }
    val formattedDate = remember(item.entry.dateTimestamp, strings.isZh) {
        dateFormat.format(Date(item.entry.dateTimestamp))
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = item.photoPath,
                contentDescription = item.entry.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Bottom Gradient Overlay with Date
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.55f))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                        color = Color.White
                    )
                    Text(
                        text = item.entry.moodType.emoji,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

// ==================== 3. ONE YEAR AGO TODAY ====================
@Composable
private fun OneYearAgoSection(
    allEntries: List<DiaryEntry>,
    referenceDateMillis: Long,
    viewModel: DiaryViewModel,
    onNavigateDetail: (DiaryEntry) -> Unit,
    onNewEntryForDate: (Long) -> Unit,
    onChangeReferenceDate: (Long) -> Unit
) {
    val strings = LocalAppStrings.current
    val oneYearAgoTimestamp = remember(referenceDateMillis) {
        viewModel.getOneYearAgoTimestamp(referenceDateMillis)
    }
    val oneYearAgoEntries = remember(allEntries, referenceDateMillis) {
        viewModel.getOneYearAgoEntries(allEntries, referenceDateMillis)
    }

    val displayDateFormat = remember(strings.isZh) {
        if (strings.isZh) SimpleDateFormat("yyyy年M月d日", Locale.CHINA)
        else SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH)
    }
    val targetDateString = remember(oneYearAgoTimestamp, strings.isZh) {
        displayDateFormat.format(Date(oneYearAgoTimestamp))
    }
    val currentDateString = remember(referenceDateMillis, strings.isZh) {
        displayDateFormat.format(Date(referenceDateMillis))
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("analytics_one_year_ago"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.analyticsOneYearAgoTitle,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = strings.analyticsOneYearAgoDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Date Navigator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                // Previous day
                                onChangeReferenceDate(referenceDateMillis - 86400000L)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = strings.analyticsPrevDay,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = String.format(strings.analyticsTargetDate, targetDateString),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = String.format(strings.analyticsBasedOnDate, currentDateString),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = {
                                // Next day
                                onChangeReferenceDate(referenceDateMillis + 86400000L)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = strings.analyticsNextDay,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Reset to today button if shifted
                    val isToday = remember(referenceDateMillis) {
                        val calNow = Calendar.getInstance()
                        val calRef = Calendar.getInstance().apply { timeInMillis = referenceDateMillis }
                        calNow.get(Calendar.YEAR) == calRef.get(Calendar.YEAR) &&
                                calNow.get(Calendar.DAY_OF_YEAR) == calRef.get(Calendar.DAY_OF_YEAR)
                    }

                    if (!isToday) {
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedButton(
                            onClick = { onChangeReferenceDate(System.currentTimeMillis()) },
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Today, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(strings.analyticsBackToToday, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        // Content: One year ago entries or empty reminder
        if (oneYearAgoEntries.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = String.format(strings.analyticsMemoriesOnDate, oneYearAgoEntries.size),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            items(oneYearAgoEntries, key = { it.id }) { entry ->
                DiaryCard(
                    entry = entry,
                    onClick = { onNavigateDetail(entry) },
                    onToggleFavorite = { viewModel.toggleFavorite(entry) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Today,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = strings.analyticsNoMemoriesOnDate,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = String.format(strings.analyticsNoMemoriesDesc, targetDateString),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    onNewEntryForDate(oneYearAgoTimestamp)
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(strings.analyticsRecordMemoryForDate)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== PHOTO VIEWER DIALOG ====================
@Composable
private fun PhotoViewerDialog(
    item: GalleryPhotoItem,
    onDismiss: () -> Unit,
    onOpenEntry: () -> Unit
) {
    val strings = LocalAppStrings.current
    val dateFormat = remember(strings.isZh) {
        if (strings.isZh) SimpleDateFormat("yyyy年M月d日 HH:mm", Locale.CHINA)
        else SimpleDateFormat("MMM d, yyyy, h:mm a", Locale.ENGLISH)
    }
    val formattedDate = remember(item.entry.dateTimestamp, strings.isZh) {
        dateFormat.format(Date(item.entry.dateTimestamp))
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Top Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.entry.title.ifBlank { strings.untitledEntry },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = strings.close)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // High-resolution image preview
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    AsyncImage(
                        model = item.photoPath,
                        contentDescription = item.entry.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mood & Weather badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = item.entry.moodType.badgeColor.copy(alpha = 0.2f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = item.entry.moodType.emoji, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = item.entry.moodType.getLocalizedName(strings.isZh),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = item.entry.weatherType.badgeColor.copy(alpha = 0.2f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = item.entry.weatherType.icon,
                                contentDescription = null,
                                tint = item.entry.weatherType.badgeColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = item.entry.weatherType.getLocalizedName(strings.isZh),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Navigation button to open the diary entry
                Button(
                    onClick = onOpenEntry,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strings.analyticsOpenFullEntry)
                }
            }
        }
    }
}
