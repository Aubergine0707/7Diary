package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioPlayerManager
import com.example.audio.AudioRecorderManager
import com.example.data.local.AppDatabase
import com.example.data.local.SecurityPreferences
import com.example.data.model.DiaryEntry
import com.example.data.model.MoodType
import com.example.data.model.SevenDayCycle
import com.example.data.model.SevenDayCycleHelper
import com.example.data.model.WeatherType
import com.example.data.repository.DiaryRepository
import com.example.security.BiometricAuthManager
import com.example.ui.theme.MaterialColorPalette
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

enum class HomeTab {
    HOME,
    CYCLE,
    TIMELINE,
    CALENDAR,
    ANALYTICS
}

enum class AnalyticsTimeRange(val displayName: String, val shortName: String) {
    WEEK("Past Week", "7D"),
    MONTH("Past Month", "30D"),
    YEAR("Past Year", "1Y"),
    ALL("All Time", "All")
}

data class GalleryPhotoItem(
    val photoPath: String,
    val entry: DiaryEntry
)

data class MoodStat(
    val mood: MoodType,
    val count: Int,
    val percentage: Float
)

data class WeatherStat(
    val weather: WeatherType,
    val count: Int,
    val percentage: Float
)

class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DiaryRepository
    val securityPrefs = SecurityPreferences(application)
    val biometricAuthManager = BiometricAuthManager(application)
    val audioRecorder = AudioRecorderManager(application)
    val audioPlayer = AudioPlayerManager()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = DiaryRepository(database.diaryDao())
    }

    val allEntries: StateFlow<List<DiaryEntry>> = repository.allEntries
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 7-Day Cycle state
    private val _currentWeekOffset = MutableStateFlow(0)
    val currentWeekOffset: StateFlow<Int> = _currentWeekOffset.asStateFlow()

    private val _cycleReflection = MutableStateFlow("")
    val cycleReflection: StateFlow<String> = _cycleReflection.asStateFlow()

    val currentSevenDayCycle: StateFlow<SevenDayCycle> = combine(
        allEntries,
        _currentWeekOffset,
        _cycleReflection
    ) { entries, offset, refl ->
        SevenDayCycleHelper.getCycleForWeekOffset(offset, entries, refl)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SevenDayCycleHelper.getCycleForWeekOffset(0, emptyList())
    )

    // Security lock state
    private val _isAppLocked = MutableStateFlow(securityPrefs.isLockEnabled)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    // App Theme State ("SYSTEM", "LIGHT", "DARK")
    private val _themeMode = MutableStateFlow(securityPrefs.themeMode)
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    // Material Color Palette State
    private val _colorPalette = MutableStateFlow(
        MaterialColorPalette.fromId(securityPrefs.colorPalette)
    )
    val colorPalette: StateFlow<MaterialColorPalette> = _colorPalette.asStateFlow()

    // App Language State ("ZH", "EN")
    private val _appLanguage = MutableStateFlow(securityPrefs.appLanguage)
    val appLanguage: StateFlow<String> = _appLanguage.asStateFlow()

    fun setLanguage(lang: String) {
        securityPrefs.appLanguage = lang
        _appLanguage.value = lang
    }

    // Home Tab
    private val _currentTab = MutableStateFlow(HomeTab.HOME)
    val currentTab: StateFlow<HomeTab> = _currentTab.asStateFlow()

    // Filter states
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedWeatherFilter = MutableStateFlow<WeatherType?>(null)
    val selectedWeatherFilter: StateFlow<WeatherType?> = _selectedWeatherFilter.asStateFlow()

    private val _selectedMoodFilter = MutableStateFlow<MoodType?>(null)
    val selectedMoodFilter: StateFlow<MoodType?> = _selectedMoodFilter.asStateFlow()

    private val _selectedCalendarDate = MutableStateFlow<Long?>(null)
    val selectedCalendarDate: StateFlow<Long?> = _selectedCalendarDate.asStateFlow()

    // Filtered entries combining all filters
    val filteredEntries: StateFlow<List<DiaryEntry>> = combine(
        allEntries,
        _searchQuery,
        _selectedWeatherFilter,
        _selectedMoodFilter,
        _selectedCalendarDate
    ) { entries, query, weatherFilter, moodFilter, calDate ->
        entries.filter { entry ->
            val matchesQuery = query.isBlank() ||
                    entry.title.contains(query, ignoreCase = true) ||
                    entry.content.contains(query, ignoreCase = true)

            val matchesWeather = weatherFilter == null || entry.weatherType == weatherFilter
            val matchesMood = moodFilter == null || entry.moodType == moodFilter

            val matchesDate = if (calDate == null) {
                true
            } else {
                val cal1 = Calendar.getInstance().apply { timeInMillis = entry.dateTimestamp }
                val cal2 = Calendar.getInstance().apply { timeInMillis = calDate }
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                        cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
            }

            matchesQuery && matchesWeather && matchesMood && matchesDate
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Current Entry Editor State
    data class EditorUiState(
        val entryId: Long = 0,
        val isEditing: Boolean = false,
        val dateTimestamp: Long = System.currentTimeMillis(),
        val weather: WeatherType = WeatherType.SUNNY,
        val mood: MoodType = MoodType.HAPPY,
        val title: String = "",
        val contentValue: TextFieldValue = TextFieldValue(""),
        val photos: List<String> = emptyList(),
        val audioPath: String? = null,
        val audioDurationSec: Int = 0,
        val isFavorite: Boolean = false,
        val isSavedSuccess: Boolean = false
    )

    private val _editorState = MutableStateFlow(EditorUiState())
    val editorState: StateFlow<EditorUiState> = _editorState.asStateFlow()

    fun setCurrentTab(tab: HomeTab) {
        _currentTab.value = tab
    }

    // 7-Day Cycle Navigation and Reflection
    fun setWeekOffset(offset: Int) {
        _currentWeekOffset.value = offset
        val cycle = SevenDayCycleHelper.getCycleForWeekOffset(offset, allEntries.value)
        _cycleReflection.value = securityPrefs.getCycleReflection(cycle.cycleKey)
    }

    fun nextWeekCycle() {
        setWeekOffset(_currentWeekOffset.value + 1)
    }

    fun previousWeekCycle() {
        setWeekOffset(_currentWeekOffset.value - 1)
    }

    fun resetToCurrentCycle() {
        setWeekOffset(0)
    }

    fun saveCycleReflection(text: String) {
        val cycle = currentSevenDayCycle.value
        securityPrefs.setCycleReflection(cycle.cycleKey, text)
        _cycleReflection.value = text
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setWeatherFilter(weather: WeatherType?) {
        _selectedWeatherFilter.value = weather
    }

    fun setMoodFilter(mood: MoodType?) {
        _selectedMoodFilter.value = mood
    }

    fun setSelectedCalendarDate(date: Long?) {
        _selectedCalendarDate.value = date
    }

    // Security methods
    fun unlockApp() {
        _isAppLocked.value = false
    }

    fun lockApp() {
        if (securityPrefs.isLockEnabled) {
            _isAppLocked.value = true
        }
    }

    fun verifyPin(pin: String): Boolean {
        val valid = securityPrefs.verifyPin(pin)
        if (valid) {
            unlockApp()
        }
        return valid
    }

    fun setPin(pin: String) {
        securityPrefs.setPin(pin)
        _isAppLocked.value = false
    }

    fun setLockEnabled(enabled: Boolean) {
        securityPrefs.isLockEnabled = enabled
        if (!enabled) {
            _isAppLocked.value = false
        }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        securityPrefs.isBiometricEnabled = enabled
    }

    fun clearSecurity() {
        securityPrefs.clearSecurityLock()
        _isAppLocked.value = false
    }

    fun setThemeMode(mode: String) {
        securityPrefs.themeMode = mode
        _themeMode.value = mode
    }

    fun setColorPalette(palette: MaterialColorPalette) {
        securityPrefs.colorPalette = palette.id
        _colorPalette.value = palette
    }

    // Toggle favorite
    fun toggleFavorite(entry: DiaryEntry) {
        viewModelScope.launch {
            repository.updateEntry(entry.copy(isFavorite = !entry.isFavorite))
        }
    }

    fun deleteEntry(entry: DiaryEntry) {
        viewModelScope.launch {
            // Clean up files
            entry.photoList.forEach { path ->
                try { File(path).delete() } catch (e: Exception) { /* ignore */ }
            }
            entry.audioPath?.let { path ->
                try { File(path).delete() } catch (e: Exception) { /* ignore */ }
            }
            repository.deleteEntry(entry)
        }
    }

    // Editor Actions
    fun prepareNewEntry(initialTimestamp: Long = System.currentTimeMillis()) {
        audioPlayer.stop()
        audioRecorder.stopRecording(discard = true)
        _editorState.value = EditorUiState(
            entryId = 0,
            isEditing = false,
            dateTimestamp = initialTimestamp,
            weather = WeatherType.SUNNY,
            mood = MoodType.HAPPY,
            title = "",
            contentValue = TextFieldValue(""),
            photos = emptyList(),
            audioPath = null,
            audioDurationSec = 0,
            isFavorite = false,
            isSavedSuccess = false
        )
    }

    fun prepareEditEntry(entry: DiaryEntry) {
        audioPlayer.stop()
        audioRecorder.stopRecording(discard = true)
        _editorState.value = EditorUiState(
            entryId = entry.id,
            isEditing = true,
            dateTimestamp = entry.dateTimestamp,
            weather = entry.weatherType,
            mood = entry.moodType,
            title = entry.title,
            contentValue = TextFieldValue(entry.content, TextRange(entry.content.length)),
            photos = entry.photoList,
            audioPath = entry.audioPath,
            audioDurationSec = entry.audioDurationSeconds,
            isFavorite = entry.isFavorite,
            isSavedSuccess = false
        )
    }

    fun updateEditorWeather(weather: WeatherType) {
        _editorState.value = _editorState.value.copy(weather = weather)
    }

    fun updateEditorMood(mood: MoodType) {
        _editorState.value = _editorState.value.copy(mood = mood)
    }

    fun updateEditorDate(timestamp: Long) {
        _editorState.value = _editorState.value.copy(dateTimestamp = timestamp)
    }

    fun updateEditorTitle(title: String) {
        _editorState.value = _editorState.value.copy(title = title)
    }

    fun updateEditorContent(content: TextFieldValue) {
        _editorState.value = _editorState.value.copy(contentValue = content)
    }

    fun addPhotoUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val dir = File(context.filesDir, "diary_images")
                if (!dir.exists()) dir.mkdirs()
                val destFile = File(dir, "img_${System.currentTimeMillis()}.jpg")

                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(destFile).use { output ->
                        input.copyTo(output)
                    }
                }
                val currentPhotos = _editorState.value.photos.toMutableList()
                currentPhotos.add(destFile.absolutePath)
                _editorState.value = _editorState.value.copy(photos = currentPhotos)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun removePhoto(index: Int) {
        val currentPhotos = _editorState.value.photos.toMutableList()
        if (index in currentPhotos.indices) {
            val removed = currentPhotos.removeAt(index)
            try { File(removed).delete() } catch (e: Exception) { /* ignore */ }
            _editorState.value = _editorState.value.copy(photos = currentPhotos)
        }
    }

    // Audio actions
    fun startRecordingVoice(): Boolean {
        audioPlayer.stop()
        return audioRecorder.startRecording()
    }

    fun stopRecordingVoice() {
        val path = audioRecorder.stopRecording(discard = false)
        val duration = audioRecorder.state.value.durationSeconds
        if (!path.isNullOrBlank()) {
            _editorState.value = _editorState.value.copy(
                audioPath = path,
                audioDurationSec = duration
            )
        }
    }

    fun cancelRecordingVoice() {
        audioRecorder.stopRecording(discard = true)
    }

    fun deleteEditorAudio() {
        audioPlayer.stop()
        val path = _editorState.value.audioPath
        if (!path.isNullOrBlank()) {
            try { File(path).delete() } catch (e: Exception) { /* ignore */ }
        }
        _editorState.value = _editorState.value.copy(audioPath = null, audioDurationSec = 0)
    }

    fun playAudio(path: String) {
        audioPlayer.play(path)
    }

    fun pauseAudio() {
        audioPlayer.pause()
    }

    fun seekAudio(posMs: Int) {
        audioPlayer.seekTo(posMs)
    }

    fun saveCurrentEntry(onSaved: () -> Unit) {
        viewModelScope.launch {
            val current = _editorState.value
            val entry = DiaryEntry(
                id = current.entryId,
                title = current.title.trim().ifBlank { "Untitled Entry" },
                content = current.contentValue.text,
                dateTimestamp = current.dateTimestamp,
                weather = current.weather.name,
                mood = current.mood.name,
                photoPaths = DiaryEntry.createPhotoPathsString(current.photos),
                audioPath = current.audioPath,
                audioDurationSeconds = current.audioDurationSec,
                isFavorite = current.isFavorite,
                updatedAt = System.currentTimeMillis()
            )

            if (current.isEditing) {
                repository.updateEntry(entry)
            } else {
                repository.insertEntry(entry)
            }
            _editorState.value = _editorState.value.copy(isSavedSuccess = true)
            onSaved()
        }
    }

    // ==================== DATA ANALYTICS & INSIGHTS ====================
    private val _analyticsTimeRange = MutableStateFlow(AnalyticsTimeRange.ALL)
    val analyticsTimeRange: StateFlow<AnalyticsTimeRange> = _analyticsTimeRange.asStateFlow()

    private val _oneYearAgoReferenceDate = MutableStateFlow(System.currentTimeMillis())
    val oneYearAgoReferenceDate: StateFlow<Long> = _oneYearAgoReferenceDate.asStateFlow()

    fun setAnalyticsTimeRange(range: AnalyticsTimeRange) {
        _analyticsTimeRange.value = range
    }

    fun setOneYearAgoReferenceDate(dateMillis: Long) {
        _oneYearAgoReferenceDate.value = dateMillis
    }

    /**
     * Filter entries according to the selected time range (past week, month, year, or all)
     */
    fun filterEntriesByTimeRange(
        entries: List<DiaryEntry>,
        range: AnalyticsTimeRange,
        referenceNow: Long = System.currentTimeMillis()
    ): List<DiaryEntry> {
        return when (range) {
            AnalyticsTimeRange.WEEK -> {
                val cutoff = referenceNow - (7L * 24L * 60L * 60L * 1000L)
                entries.filter { it.dateTimestamp >= cutoff }
            }
            AnalyticsTimeRange.MONTH -> {
                val cutoff = referenceNow - (30L * 24L * 60L * 60L * 1000L)
                entries.filter { it.dateTimestamp >= cutoff }
            }
            AnalyticsTimeRange.YEAR -> {
                val cutoff = referenceNow - (365L * 24L * 60L * 60L * 1000L)
                entries.filter { it.dateTimestamp >= cutoff }
            }
            AnalyticsTimeRange.ALL -> entries
        }
    }

    /**
     * Compute mood statistics and proportional percentages
     */
    fun computeMoodStats(entries: List<DiaryEntry>): List<MoodStat> {
        if (entries.isEmpty()) return emptyList()
        val total = entries.size.toFloat()
        val counts = entries.groupBy { it.moodType }
            .mapValues { it.value.size }

        return MoodType.entries.map { mood ->
            val count = counts[mood] ?: 0
            val percentage = if (total > 0) (count / total) * 100f else 0f
            MoodStat(mood, count, percentage)
        }.filter { it.count > 0 }
            .sortedByDescending { it.count }
    }

    /**
     * Compute weather statistics and proportional percentages
     */
    fun computeWeatherStats(entries: List<DiaryEntry>): List<WeatherStat> {
        if (entries.isEmpty()) return emptyList()
        val total = entries.size.toFloat()
        val counts = entries.groupBy { it.weatherType }
            .mapValues { it.value.size }

        return WeatherType.entries.map { weather ->
            val count = counts[weather] ?: 0
            val percentage = if (total > 0) (count / total) * 100f else 0f
            WeatherStat(weather, count, percentage)
        }.filter { it.count > 0 }
            .sortedByDescending { it.count }
    }

    /**
     * Extract all photos uploaded across diary entries into gallery photo items
     */
    fun extractGalleryPhotos(entries: List<DiaryEntry>): List<GalleryPhotoItem> {
        val list = mutableListOf<GalleryPhotoItem>()
        // Sorted chronologically descending (newest first)
        entries.sortedByDescending { it.dateTimestamp }.forEach { entry ->
            entry.photoList.forEach { path ->
                list.add(GalleryPhotoItem(path, entry))
            }
        }
        return list
    }

    /**
     * Find entries recorded on exactly the same day 1 year ago
     */
    fun getOneYearAgoEntries(
        entries: List<DiaryEntry>,
        referenceDateMillis: Long = _oneYearAgoReferenceDate.value
    ): List<DiaryEntry> {
        val targetCal = Calendar.getInstance().apply {
            timeInMillis = referenceDateMillis
            add(Calendar.YEAR, -1)
        }
        val targetYear = targetCal.get(Calendar.YEAR)
        val targetMonth = targetCal.get(Calendar.MONTH)
        val targetDay = targetCal.get(Calendar.DAY_OF_MONTH)

        return entries.filter { entry ->
            val entryCal = Calendar.getInstance().apply { timeInMillis = entry.dateTimestamp }
            entryCal.get(Calendar.YEAR) == targetYear &&
                    entryCal.get(Calendar.MONTH) == targetMonth &&
                    entryCal.get(Calendar.DAY_OF_MONTH) == targetDay
        }
    }

    /**
     * Get timestamp corresponding to 1 year ago today
     */
    fun getOneYearAgoTimestamp(referenceDateMillis: Long = _oneYearAgoReferenceDate.value): Long {
        return Calendar.getInstance().apply {
            timeInMillis = referenceDateMillis
            add(Calendar.YEAR, -1)
        }.timeInMillis
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
        audioRecorder.stopRecording(discard = true)
    }
}
