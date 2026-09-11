package com.example.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

class AudioRecorderManager(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var currentOutputFile: File? = null
    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    data class RecorderState(
        val isRecording: Boolean = false,
        val durationSeconds: Int = 0,
        val maxAmplitude: Int = 0,
        val recordedFilePath: String? = null
    )

    private val _state = MutableStateFlow(RecorderState())
    val state: StateFlow<RecorderState> = _state.asStateFlow()

    fun startRecording(): Boolean {
        try {
            stopRecording(discard = true)

            val dir = File(context.filesDir, "diary_recordings")
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val file = File(dir, "voice_${System.currentTimeMillis()}.m4a")
            currentOutputFile = file

            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }

            _state.value = RecorderState(
                isRecording = true,
                durationSeconds = 0,
                maxAmplitude = 0,
                recordedFilePath = file.absolutePath
            )

            startTimer()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            cleanup()
            return false
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch {
            var seconds = 0
            while (isActive && _state.value.isRecording) {
                delay(200)
                val amp = try {
                    recorder?.maxAmplitude ?: 0
                } catch (e: Exception) {
                    0
                }
                // Update every ~1 second for duration, more frequently for amplitude
                if (System.currentTimeMillis() % 1000 < 250) {
                    seconds++
                }
                _state.value = _state.value.copy(
                    durationSeconds = seconds,
                    maxAmplitude = amp
                )
            }
        }
    }

    fun stopRecording(discard: Boolean = false): String? {
        timerJob?.cancel()
        timerJob = null

        val path = currentOutputFile?.absolutePath
        try {
            recorder?.apply {
                try {
                    stop()
                } catch (e: Exception) {
                    // Ignore stop error if recorded too briefly
                }
                reset()
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            recorder = null
        }

        if (discard) {
            currentOutputFile?.delete()
            currentOutputFile = null
            _state.value = RecorderState()
            return null
        }

        val finalDuration = _state.value.durationSeconds
        _state.value = RecorderState(
            isRecording = false,
            durationSeconds = finalDuration,
            recordedFilePath = path
        )
        return path
    }

    private fun cleanup() {
        timerJob?.cancel()
        timerJob = null
        try {
            recorder?.release()
        } catch (e: Exception) {
            // ignore
        }
        recorder = null
        _state.value = RecorderState()
    }
}
