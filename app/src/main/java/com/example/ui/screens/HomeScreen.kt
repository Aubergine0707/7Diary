package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.DiaryEntry
import com.example.data.model.MoodType
import com.example.data.model.WeatherType
import com.example.ui.components.CalendarMonthView
import com.example.ui.components.DiaryCard
import com.example.ui.components.SevenDayCycleView
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
                                text = "7Diary",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "7-Day Cycle Journal",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    // Search toggle
                    IconButton(
                        onClick = { isSearchActive = !isSearchActive },
                        modifier = Modifier.testTag("btn_toggle_search")
                    ) {
                        Icon(
                            imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }

                    // Filter toggle
                    IconButton(
                        onClick = { isFilterActive = !isFilterActive },
                        modifier = Modifier.testTag("btn_toggle_filter")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter",
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
                                contentDescription = "Lock App",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Settings
                    IconButton(
                        onClick = onNavigateSettings,
                        modifier = Modifier.testTag("btn_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
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
                    contentDescription = "Add New Diary Entry",
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
            // Search Input Field
            AnimatedVisibility(visible = isSearchActive && currentTab != HomeTab.ANALYTICS) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Search by title or text...") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Clear search")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
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
            AnimatedVisibility(visible = isFilterActive && currentTab != HomeTab.ANALYTICS) {
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
                            text = "Mood:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        ElevatedFilterChip(
                            selected = moodFilter == null,
                            onClick = { viewModel.setMoodFilter(null) },
                            label = { Text("All") }
                        )
                        MoodType.entries.forEach { mood ->
                            ElevatedFilterChip(
                                selected = moodFilter == mood,
                                onClick = {
                                    viewModel.setMoodFilter(if (moodFilter == mood) null else mood)
                                },
                                label = { Text("${mood.emoji} ${mood.displayName}") },
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
                            text = "Weather:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        ElevatedFilterChip(
                            selected = weatherFilter == null,
                            onClick = { viewModel.setWeatherFilter(null) },
                            label = { Text("All") }
                        )
                        WeatherType.entries.forEach { weather ->
                            ElevatedFilterChip(
                                selected = weatherFilter == weather,
                                onClick = {
                                    viewModel.setWeatherFilter(if (weatherFilter == weather) null else weather)
                                },
                                label = { Text(weather.displayName) },
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

            // 7-Day Cycle vs Timeline vs Calendar vs Analytics View Switcher
            ScrollableTabRow(
                selectedTabIndex = currentTab.ordinal,
                containerColor = MaterialTheme.colorScheme.surface,
                edgePadding = 12.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Tab(
                    selected = currentTab == HomeTab.CYCLE,
                    onClick = { viewModel.setCurrentTab(HomeTab.CYCLE) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ViewWeek,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("7-Day Unit")
                        }
                    },
                    modifier = Modifier.testTag("tab_cycle")
                )

                Tab(
                    selected = currentTab == HomeTab.TIMELINE,
                    onClick = { viewModel.setCurrentTab(HomeTab.TIMELINE) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Timeline")
                        }
                    },
                    modifier = Modifier.testTag("tab_timeline")
                )

                Tab(
                    selected = currentTab == HomeTab.CALENDAR,
                    onClick = { viewModel.setCurrentTab(HomeTab.CALENDAR) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Calendar")
                        }
                    },
                    modifier = Modifier.testTag("tab_calendar")
                )

                Tab(
                    selected = currentTab == HomeTab.ANALYTICS,
                    onClick = { viewModel.setCurrentTab(HomeTab.ANALYTICS) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Insights,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Analytics")
                        }
                    },
                    modifier = Modifier.testTag("tab_analytics")
                )
            }

            // Main Content: 7-Day Cycle, Timeline, Calendar, or Analytics
            when (currentTab) {
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
    if (entries.isEmpty()) {
        EmptyDiaryState(
            message = "No diary entries found.\nTap the + button below to write your first entry!"
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
    val dateDisplayFormat = remember { SimpleDateFormat("MMMM d, yyyy", Locale.US) }

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
                "Entries on ${dateDisplayFormat.format(Date(selectedDate))} (${filteredEntries.size})"
            } else {
                "All Journal Entries (${allEntries.size})"
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
                        "No diary entries recorded on this day.\nTap + to create one!"
                    } else {
                        "No diary entries yet.\nTap + to write your first diary!"
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
            text = "Your 7-Day Journal Awaits",
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
