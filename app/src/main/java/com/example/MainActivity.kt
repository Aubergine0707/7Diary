package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DiaryEntry
import com.example.ui.components.LockScreenView
import com.example.ui.screens.DiaryDetailScreen
import com.example.ui.screens.DiaryEditorScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.DiaryViewModel

sealed interface AppDestination {
    data object Home : AppDestination
    data object Editor : AppDestination
    data class Detail(val entry: DiaryEntry) : AppDestination
    data object Settings : AppDestination
}

class MainActivity : FragmentActivity() {

    private val viewModel: DiaryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val colorPalette by viewModel.colorPalette.collectAsStateWithLifecycle()
            val isLocked by viewModel.isAppLocked.collectAsStateWithLifecycle()

            MyApplicationTheme(themeMode = themeMode, palette = colorPalette) {
                var currentDestination by remember { mutableStateOf<AppDestination>(AppDestination.Home) }

                // Auto-trigger biometric prompt if locked and biometric enabled
                val isBiometricHardwareAvailable = remember {
                    viewModel.biometricAuthManager.isBiometricHardwareAvailable()
                }
                val isBiometricEnabled = viewModel.securityPrefs.isBiometricEnabled

                fun launchBiometricPrompt() {
                    if (isBiometricHardwareAvailable && isBiometricEnabled) {
                        viewModel.biometricAuthManager.authenticate(
                            activity = this@MainActivity,
                            title = "Unlock Offline Diary",
                            subtitle = "Use your fingerprint or biometric credential",
                            negativeButtonText = "Use PIN Passcode",
                            onSuccess = {
                                viewModel.unlockApp()
                            },
                            onError = { _, errString ->
                                // Cancelled or switched to PIN
                            },
                            onFailed = {
                                Toast.makeText(this@MainActivity, "Biometric verification failed", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }

                LaunchedEffect(isLocked) {
                    if (isLocked && isBiometricHardwareAvailable && isBiometricEnabled) {
                        launchBiometricPrompt()
                    }
                }

                if (isLocked) {
                    LockScreenView(
                        isBiometricAvailable = isBiometricHardwareAvailable,
                        isBiometricEnabled = isBiometricEnabled,
                        onVerifyPin = { pin ->
                            viewModel.verifyPin(pin)
                        },
                        onBiometricRequest = {
                            launchBiometricPrompt()
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Back handler
                    BackHandler(enabled = currentDestination !is AppDestination.Home) {
                        currentDestination = AppDestination.Home
                    }

                    AnimatedContent(
                        targetState = currentDestination,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "screen_transition"
                    ) { destination ->
                        when (destination) {
                            is AppDestination.Home -> {
                                HomeScreen(
                                    viewModel = viewModel,
                                    onNavigateNewEntry = {
                                        viewModel.prepareNewEntry()
                                        currentDestination = AppDestination.Editor
                                    },
                                    onNavigateDetail = { entry ->
                                        currentDestination = AppDestination.Detail(entry)
                                    },
                                    onNavigateSettings = {
                                        currentDestination = AppDestination.Settings
                                    }
                                )
                            }

                            is AppDestination.Editor -> {
                                DiaryEditorScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = {
                                        currentDestination = AppDestination.Home
                                    }
                                )
                            }

                            is AppDestination.Detail -> {
                                DiaryDetailScreen(
                                    entry = destination.entry,
                                    viewModel = viewModel,
                                    onNavigateBack = {
                                        currentDestination = AppDestination.Home
                                    },
                                    onNavigateEdit = { entry ->
                                        viewModel.prepareEditEntry(entry)
                                        currentDestination = AppDestination.Editor
                                    }
                                )
                            }

                            is AppDestination.Settings -> {
                                SettingsScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = {
                                        currentDestination = AppDestination.Home
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // If security lock is enabled, lock app when backgrounded for maximum user privacy
        if (viewModel.securityPrefs.isLockEnabled) {
            viewModel.lockApp()
        }
    }
}
