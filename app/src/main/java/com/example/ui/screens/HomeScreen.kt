package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DiaryEntry
import com.example.data.model.MoodType
import com.example.data.model.WeatherType
import com.example.ui.components.CalendarMonthView
import com.example.ui.components.DiaryCard
import com.example.ui.components.HomeDashboardView
import com.example.ui.components.SevenDayCycleView
import com.example.util.LocalAppStrings
import com.example.util.getLocalizedName
import com.example.viewmodel.DiaryViewModel
import com.example.viewmodel.HomeTab
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: DiaryViewModel,
    onNavigateNewEntry: () -> Unit,
    onNavigateDetail: (DiaryEntry) -> Unit,
    onNavigateSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val allEntries by viewModel.allEntries.collectAsStateWithLifecycle()
    val filteredEntries by viewModel.filteredEntries.collectAsStateWithLifecycle()
    val currentSevenDayCycle by viewModel.currentSevenDayCycle.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val weatherFilter by viewModel.selectedWeatherFilter.collectAsStateWithLifecycle()
    val moodFilter by viewModel.selectedMoodFilter.collectAsStateWithLifecycle()
    val selectedCalendarDate by viewModel.selectedCalendarDate.collectAsStateWithLifecycle()

    var isSearchActive by remember { mutableStateOf(false) }
    var isFilterActive by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "7",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = strings.appName,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = when (currentTab) {
                                    HomeTab.HOME -> strings.navHome
                                    HomeTab.CYCLE -> strings.navCycle
                                    HomeTab.TIMELINE -> strings.navTimeline
                                    HomeTab.CALENDAR -> strings.navCalendar
                                    HomeTab.ANALYTICS -> strings.navAnalytics
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    // Search toggle (applicable across entries)
                    IconButton(
                        onClick = {
                            isSearchActive = !isSearchActive
                            if (isSearchActive && (currentTab == HomeTab.HOME || currentTab == HomeTab.ANALYTICS)) {
                                viewModel.setCurrentTab(HomeTab.TIMELINE)
                            }
                        },
                        modifier = Modifier.testTag("btn_toggle_search")
                    ) {
                        Icon(
                            imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = strings.searchPlaceholder
                        )
                    }

                    // Filter toggle
                    IconButton(
                        onClick = {
                            isFilterActive = !isFilterActive
                            if (isFilterActive && (currentTab == HomeTab.HOME || currentTab == HomeTab.ANALYTICS)) {
                                viewModel.setCurrentTab(HomeTab.TIMELINE)
                            }
                        },
                        modifier = Modifier.testTag("btn_toggle_filter")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = if (strings.isZh) "筛选" else "Filter",
                            tint = if (weatherFilter != null || moodFilter != null) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }

                    // Quick Lock button (if lock is configured)
                    if (viewModel.securityPrefs.isLockEnabled) {
                        IconButton(
                            onClick = { viewModel.lockApp() },
                            modifier = Modifier.testTag("btn_quick_lock")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = strings.settingsLockTitle,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            // Google M3 Bottom Navigation Bar with ONLY ICON BUTTONS as requested
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                tonalElevation = 3.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = currentTab == HomeTab.HOME,
                    onClick = { viewModel.setCurrentTab(HomeTab.HOME) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = strings.navHome,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    alwaysShowLabel = false,
                    modifier = Modifier.testTag("bottom_tab_home")
                )

                NavigationBarItem(
                    selected = currentTab == HomeTab.CYCLE,
                    onClick = { viewModel.setCurrentTab(HomeTab.CYCLE) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.ViewWeek,
                            contentDescription = strings.navCycle,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    alwaysShowLabel = false,
                    modifier = Modifier.testTag("bottom_tab_cycle")
                )

                NavigationBarItem(
                    selected = currentTab == HomeTab.TIMELINE,
                    onClick = { viewModel.setCurrentTab(HomeTab.TIMELINE) },
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.FormatListBulleted,
                            contentDescription = strings.navTimeline,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    alwaysShowLabel = false,
                    modifier = Modifier.testTag("bottom_tab_timeline")
                )

                NavigationBarItem(
                    selected = currentTab == HomeTab.CALENDAR,
                    onClick = { viewModel.setCurrentTab(HomeTab.CALENDAR) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = strings.navCalendar,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    alwaysShowLabel = false,
                    modifier = Modifier.testTag("bottom_tab_calendar")
                )

                NavigationBarItem(
                    selected = currentTab == HomeTab.ANALYTICS,
                    onClick = { viewModel.setCurrentTab(HomeTab.ANALYTICS) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Insights,
                            contentDescription = strings.navAnalytics,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    alwaysShowLabel = false,
                    modifier = Modifier.testTag("bottom_tab_analytics")
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateSettings,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = strings.navSettings,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    alwaysShowLabel = false,
                    modifier = Modifier.testTag("bottom_tab_settings")
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateNewEntry,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_add_diary")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = strings.editorNewTitle,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input Field (Google pill style)
            AnimatedVisibility(visible = isSearchActive) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text(strings.searchPlaceholder) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = strings.cancel)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_search_query")
                    )
                }
            }

            // Weather & Mood Filters Row
            AnimatedVisibility(visible = isFilterActive) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    // Mood Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.timelineFilterMood,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        ElevatedFilterChip(
                            selected = moodFilter == null,
                            onClick = { viewModel.setMoodFilter(null) },
                            label = { Text(strings.all) }
                        )
                        MoodType.entries.forEach { mood ->
                            ElevatedFilterChip(
                                selected = moodFilter == mood,
                                onClick = {
                                    viewModel.setMoodFilter(if (moodFilter == mood) null else mood)
                                },
                                label = { Text("${mood.emoji} ${mood.getLocalizedName(strings.isZh)}") },
                                colors = FilterChipDefaults.elevatedFilterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Weather Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.timelineFilterWeather,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        ElevatedFilterChip(
                            selected = weatherFilter == null,
                            onClick = { viewModel.setWeatherFilter(null) },
                            label = { Text(strings.all) }
                        )
                        WeatherType.entries.forEach { weather ->
                            ElevatedFilterChip(
                                selected = weatherFilter == weather,
                                onClick = {
                                    viewModel.setWeatherFilter(if (weatherFilter == weather) null else weather)
                                },
                                label = { Text(weather.getLocalizedName(strings.isZh)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = weather.icon,
                                        contentDescription = null,
                                        tint = weather.badgeColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = FilterChipDefaults.elevatedFilterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        }
                    }
                }
            }

            // Main Content Area switched by Bottom Navigation Tabs
            when (currentTab) {
                HomeTab.HOME -> {
                    HomeDashboardView(
                        cycle = currentSevenDayCycle,
                        allEntries = allEntries,
                        onWriteClick = onNavigateNewEntry,
                        onEntryClick = onNavigateDetail,
                        onNavigateCycle = { viewModel.setCurrentTab(HomeTab.CYCLE) },
                        onNavigateTimeline = { viewModel.setCurrentTab(HomeTab.TIMELINE) }
                    )
                }

                HomeTab.CYCLE -> {
                    SevenDayCycleView(
                        cycle = currentSevenDayCycle,
                        onPreviousCycle = { viewModel.previousWeekCycle() },
                        onNextCycle = { viewModel.nextWeekCycle() },
                        onResetToCurrentCycle = { viewModel.resetToCurrentCycle() },
                        onEntryClick = onNavigateDetail,
                        onNewEntryForDate = { timestamp ->
                            viewModel.prepareNewEntry(timestamp)
                            onNavigateNewEntry()
                        },
                        onSaveReflection = { reflection ->
                            viewModel.saveCycleReflection(reflection)
                        }
                    )
                }

                HomeTab.TIMELINE -> {
                    TimelineViewContent(
                        entries = filteredEntries,
                        onEntryClick = onNavigateDetail,
                        onToggleFavorite = { viewModel.toggleFavorite(it) }
                    )
                }

                HomeTab.CALENDAR -> {
                    CalendarViewContent(
                        allEntries = allEntries,
                        filteredEntries = filteredEntries,
                        selectedDate = selectedCalendarDate,
                        onDateSelected = { viewModel.setSelectedCalendarDate(it) },
                        onEntryClick = onNavigateDetail,
                        onToggleFavorite = { viewModel.toggleFavorite(it) }
                    )
                }

                HomeTab.ANALYTICS -> {
                    AnalyticsScreen(
                        viewModel = viewModel,
                        onNavigateDetail = onNavigateDetail,
                        onNewEntryForDate = { timestamp ->
                            viewModel.prepareNewEntry(timestamp)
                            onNavigateNewEntry()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineViewContent(
    entries: List<DiaryEntry>,
    onEntryClick: (DiaryEntry) -> Unit,
    onToggleFavorite: (DiaryEntry) -> Unit
) {
    val strings = LocalAppStrings.current

    if (entries.isEmpty()) {
        EmptyDiaryState(
            message = strings.timelineNoEntries
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(entries, key = { it.id }) { entry ->
                DiaryCard(
                    entry = entry,
                    onClick = { onEntryClick(entry) },
                    onToggleFavorite = onToggleFavorite
                )
            }
        }
    }
}

@Composable
private fun CalendarViewContent(
    allEntries: List<DiaryEntry>,
    filteredEntries: List<DiaryEntry>,
    selectedDate: Long?,
    onDateSelected: (Long?) -> Unit,
    onEntryClick: (DiaryEntry) -> Unit,
    onToggleFavorite: (DiaryEntry) -> Unit
) {
    val strings = LocalAppStrings.current
    val dateDisplayFormat = remember { SimpleDateFormat("yyyy年M月d日 EEEE", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            CalendarMonthView(
                entries = allEntries,
                selectedDate = selectedDate,
                onDateSelected = onDateSelected
            )
        }

        item {
            val label = if (selectedDate != null) {
                "${dateDisplayFormat.format(Date(selectedDate))} (${filteredEntries.size})"
            } else {
                "${strings.calendarTitle} (${allEntries.size})"
            }
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
            )
        }

        if (filteredEntries.isEmpty()) {
            item {
                EmptyDiaryState(
                    message = if (selectedDate != null) {
                        strings.calendarNoEntriesOnDate
                    } else {
                        strings.timelineNoEntries
                    }
                )
            }
        } else {
            items(filteredEntries, key = { it.id }) { entry ->
                DiaryCard(
                    entry = entry,
                    onClick = { onEntryClick(entry) },
                    onToggleFavorite = onToggleFavorite
                )
            }
        }
    }
}

@Composable
private fun EmptyDiaryState(message: String) {
    val strings = LocalAppStrings.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoStories,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = strings.appName,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
