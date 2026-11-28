
package com.dreamteam.breakloop.ui.main.focus

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.dreamteam.breakloop.data.local.cache.SoundscapeCache
import com.dreamteam.breakloop.data.local.entity.FocusSessionEntity
import com.dreamteam.breakloop.data.local.repository.FocusSessionRepository
import com.dreamteam.breakloop.data.local.worker.FocusSessionWorker
import com.dreamteam.breakloop.data.network.ConnectivityObserver
import com.dreamteam.breakloop.data.network.ConnectivityStatus
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class FocusViewModel(
    private val focusSessionRepository: FocusSessionRepository,
    private val appContext: Context,
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        FocusSessionUiState()
    )

    val uiState: StateFlow<FocusSessionUiState> =
        _uiState.asStateFlow()

    private var timerJob: Job? = null

    private var sessionId: String? = null

    private var sessionStartTime: Long? = null

    private val soundscapeCache = SoundscapeCache(appContext)

    private var mediaPlayer: MediaPlayer? = null

    private var soundscapeJob: Job? = null

    private val connectivityObserver = ConnectivityObserver(appContext)

    //Sprint 4
    init {
        restoreActiveSession()
    }

    private fun restoreActiveSession() {
        viewModelScope.launch {
            val uid = firebaseAuth.currentUser?.uid ?: return@launch

            try {
                val session = focusSessionRepository.getActiveSession(uid)
                    ?: return@launch

                val ahora = System.currentTimeMillis()

                val segundosTranscurridos = if (session.isPaused) {
                    0L
                } else {
                    ((ahora - session.lastUpdatedAt) / 1000).coerceAtLeast(0)
                }

                val tiempoRestante = (
                        session.remainingSeconds - segundosTranscurridos.toInt()
                        ).coerceAtLeast(0)

                val tiempoTranscurrido = (
                        session.elapsedSeconds + segundosTranscurridos.toInt()
                        ).coerceAtMost(session.duration * 60)


                sessionId = session.id
                sessionStartTime = session.startTime

                _uiState.update {
                    it.copy(
                        selectedDuration = session.duration,
                        isSessionActive = true,
                        isPaused = session.isPaused,
                        remainingSeconds = tiempoRestante,
                        elapsedSeconds = tiempoTranscurrido,
                        isLoading = false,
                        error = null
                    )
                }

                if (tiempoRestante == 0) {
                    completeSession()
                } else if (!session.isPaused) {
                    startTimer()
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Could not restore focus session"
                    )
                }
            }
        }
    }


    val connectivityStatus: StateFlow<ConnectivityStatus> =
        connectivityObserver.status
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ConnectivityStatus.CHECKING
            )

    fun onDurationChange(duration: Int) {
        _uiState.update {
            it.copy(
                selectedDuration = duration,
                isCustomDurationSelected = false,
                error = null
            )
        }
    }

    fun onCustomDurationSelected() {
        _uiState.update {
            it.copy(
                selectedDuration = it.customDuration,
                isCustomDurationSelected = true,
                error = null
            )
        }
    }

    fun onCustomDurationChange(duration: Int) {
        _uiState.update {
            it.copy(
                customDuration = duration,
                selectedDuration = duration,
                isCustomDurationSelected = true,
                error = null
            )
        }
    }

    fun onFocusGoalChange(goal: String) {
        _uiState.update {
            it.copy(
                focusGoal = goal,
                error = null
            )
        }
    }

    fun onSoundscapeChange(soundscape: Soundscape) {
        _uiState.update {
            it.copy(
                selectedSoundscape = soundscape
            )
        }
    }

    fun onAppShieldChange(enabled: Boolean) {
        _uiState.update {
            it.copy(
                appShieldEnabled = enabled
            )
        }
    }

    //Mod sprint 4
    //La sesión se inserta en Room desde el principio, con estado INPROGRESS
    fun startFocus() {
        val duration = _uiState.value.selectedDuration

        if (duration <= 0) {
            _uiState.update {
                it.copy(error = "Select a valid session duration")
            }
            return
        }

        if (_uiState.value.isCustomDurationSelected && duration > 120) {
            _uiState.update {
                it.copy(error = "Pomodoro, it's all about taking breaks!")
            }
            return
        }

        val uid = firebaseAuth.currentUser?.uid

        if (uid == null) {
            _uiState.update {
                it.copy(
                    error = "You must be logged in to start a focus session"
                )
            }
            return
        }

        timerJob?.cancel()

        sessionId = UUID.randomUUID().toString()
        sessionStartTime = System.currentTimeMillis()

        val totalSeconds = duration * 60

        val session = FocusSessionEntity(
            id = sessionId!!,
            uid = uid,
            startTime = sessionStartTime!!,
            duration = duration,
            type = "FOCUS",
            status = "IN_PROGRESS",
            xpEarned = 0,
            synced = false,
            elapsedSeconds = 0,
            remainingSeconds = totalSeconds,
            isPaused = false,
            lastUpdatedAt = System.currentTimeMillis()
        )

        _uiState.update {
            it.copy(
                isSessionActive = true,
                isLoading = true,
                isPaused = false,
                remainingSeconds = totalSeconds,
                elapsedSeconds = 0,
                error = null
            )
        }

        viewModelScope.launch {
            try {
                focusSessionRepository.saveSession(session)

                _uiState.update {
                    it.copy(isLoading = false)
                }

                startTimer()

            } catch (e: Exception) {
                sessionId = null
                sessionStartTime = null

                _uiState.update {
                    it.copy(
                        isSessionActive = false,
                        isLoading = false,
                        error = "Could not start focus session"
                    )
                }
            }
        }
    }

    //Mod sprint 4
    //Se guarda cada segundo para evitar perdidas si Android termina el proceso inesperadamente
    private fun startTimer() {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            var lastSaveTime = System.currentTimeMillis()

            while (_uiState.value.remainingSeconds > 0) {
                delay(1000)

                if (_uiState.value.isPaused) {
                    continue
                }

                _uiState.update {
                    it.copy(
                        remainingSeconds = (it.remainingSeconds - 1)
                            .coerceAtLeast(0),
                        elapsedSeconds = it.elapsedSeconds + 1
                    )
                }

                val now = System.currentTimeMillis()

                if (now - lastSaveTime >= 1000) {
                    val currentId = sessionId ?: break
                    val currentState = _uiState.value

                    try {
                        focusSessionRepository.updateSessionProgress(
                            id = currentId,
                            elapsedSeconds = currentState.elapsedSeconds,
                            remainingSeconds = currentState.remainingSeconds,
                            isPaused = currentState.isPaused,
                            lastUpdatedAt = now,
                            status = "IN_PROGRESS"
                        )

                        lastSaveTime = now
                    } catch (e: CancellationException) {
                        throw e

                    } catch (e: Exception) {
                        _uiState.update {
                            it.copy(error = "Could not save focus progress")
                        }
                    }

                }
            }

            if (
                _uiState.value.remainingSeconds == 0 &&
                _uiState.value.isSessionActive
            ) {
                completeSession()
            }
        }
    }







    fun stopFocus() {
        if (!_uiState.value.isSessionActive) {
            return
        }

        timerJob?.cancel()
        timerJob = null

        saveSession(status = "ABANDONED")

        detenerSonido()

        _uiState.update {
            it.copy(
                isSessionActive = false,
                isPaused = false,
                remainingSeconds = 0,
                elapsedSeconds = 0
            )
        }

        sessionId = null
        sessionStartTime = null
    }

    private fun completeSession() {
        println("FOCUS DEBUG: Timer completed")

        timerJob?.cancel()
        timerJob = null

        detenerSonido()

        saveSession(
            status = "COMPLETED"
        )
    }

    //Mod sprint 4
    //
    private fun saveSession(status: String) {
        val currentSessionId = sessionId ?: return
        val currentStartTime = sessionStartTime ?: return
        val currentState = _uiState.value
        val uid = firebaseAuth.currentUser?.uid ?: return

        val session = FocusSessionEntity(
            id = currentSessionId,
            uid = uid,
            startTime = currentStartTime,
            duration = currentState.selectedDuration,
            type = "FOCUS",
            status = status,
            xpEarned = if (status == "COMPLETED") 2 else 0,
            synced = false,
            elapsedSeconds = currentState.elapsedSeconds,
            remainingSeconds = currentState.remainingSeconds,
            isPaused = false,
            lastUpdatedAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            try {
                focusSessionRepository.saveSession(session)

                if (status != "IN_PROGRESS") {
                    scheduleSync()

                    _uiState.update {
                        it.copy(
                            isSessionActive = false,
                            isPaused = false,
                            remainingSeconds = 0,
                            error = null
                        )
                    }

                    sessionId = null
                    sessionStartTime = null
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Could not save focus session")
                }
            }
        }
    }
    private fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest =
            OneTimeWorkRequestBuilder<FocusSessionWorker>()
                .setConstraints(constraints)
                .build()

        WorkManager
            .getInstance(appContext)
            .enqueueUniqueWork(
                "focus-session-sync",
                ExistingWorkPolicy.KEEP,
                workRequest
            )
    }

    fun cargarSonido(nombre: String) {
        soundscapeJob?.cancel()
        detenerReproductor()

        //creación de hilo pidiendo el sonido
        soundscapeJob = viewModelScope.launch {
            try {
                val archivo = withContext(Dispatchers.IO) {
                    soundscapeCache.obtenerSonido(nombre)
                }

                val reproductor = MediaPlayer()
                mediaPlayer = reproductor

                reproductor.setOnPreparedListener { player ->
                    if (mediaPlayer === player) {
                        player.isLooping = true
                        player.start()
                    } else {
                        player.release()
                    }
                }

                reproductor.setOnErrorListener { player, _, _ ->
                    //comparación en memoria
                    if (mediaPlayer === player) {
                        mediaPlayer = null
                    }

                    player.release()
                    true
                }

                reproductor.setDataSource(archivo.absolutePath)
                reproductor.prepareAsync()

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
                detenerReproductor()
            }
        }
    }

    fun detenerSonido() {
        soundscapeJob?.cancel()
        soundscapeJob = null

        detenerReproductor()
    }

    private fun detenerReproductor() {
        val reproductor = mediaPlayer ?: return
        mediaPlayer = null

        try {
            reproductor.reset()
        } catch (e: IllegalStateException) {
            // El reproductor ya no está en un estado válido
        } finally {
            reproductor.release()
        }
    }

    override fun onCleared() {
        soundscapeJob?.cancel()
        soundscapeJob = null

        timerJob?.cancel()
        timerJob = null

        detenerReproductor()

        super.onCleared()
    }
}
