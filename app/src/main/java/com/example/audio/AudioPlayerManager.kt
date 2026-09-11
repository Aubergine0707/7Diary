package com.example.audio

import android.media.MediaPlayer
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

class AudioPlayerManager {
    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    data class PlayerState(
        val isPlaying: Boolean = false,
        val isPrepared: Boolean = false,
        val currentPositionMs: Int = 0,
        val totalDurationMs: Int = 0,
        val currentAudioPath: String? = null
    )

    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state.asStateFlow()

    fun play(filePath: String) {
        val file = File(filePath)
        if (!file.exists() || file.length() == 0L) {
            return
        }

        if (_state.value.currentAudioPath == filePath && _state.value.isPrepared) {
            mediaPlayer?.start()
            _state.value = _state.value.copy(isPlaying = true)
            startProgressUpdates()
            return
        }

        stop()

        try {
            val player = MediaPlayer().apply {
                setDataSource(filePath)
                prepare()
            }
            mediaPlayer = player

            val duration = player.duration
            _state.value = PlayerState(
                isPlaying = true,
                isPrepared = true,
                currentPositionMs = 0,
                totalDurationMs = duration,
                currentAudioPath = filePath
            )

            player.setOnCompletionListener {
                _state.value = _state.value.copy(isPlaying = false, currentPositionMs = 0)
                progressJob?.cancel()
            }

            player.start()
            startProgressUpdates()
        } catch (e: Exception) {
            e.printStackTrace()
            stop()
        }
    }

    fun pause() {
        try {
            mediaPlayer?.pause()
            _state.value = _state.value.copy(isPlaying = false)
            progressJob?.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun seekTo(positionMs: Int) {
        try {
            mediaPlayer?.seekTo(positionMs)
            _state.value = _state.value.copy(currentPositionMs = positionMs)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        progressJob?.cancel()
        progressJob = null
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                reset()
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaPlayer = null
            _state.value = PlayerState()
        }
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive && mediaPlayer?.isPlaying == true) {
                val pos = try {
                    mediaPlayer?.currentPosition ?: 0
                } catch (e: Exception) {
                    0
                }
                _state.value = _state.value.copy(currentPositionMs = pos)
                delay(200)
            }
        }
    }
}
