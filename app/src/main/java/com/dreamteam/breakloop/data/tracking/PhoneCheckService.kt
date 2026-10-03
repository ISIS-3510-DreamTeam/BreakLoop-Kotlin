package com.dreamteam.breakloop.data.tracking

import com.dreamteam.breakloop.data.local.repository.PhoneCheckRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

class PhoneCheckService(
    private val sensorDataSource: SensorDataSource,
    private val repository: PhoneCheckRepository,
    private val threshold: Double = 12.0
) {

    companion object {
        const val BASELINE_PICKUPS = 60

        private const val MIN_DETECTION_INTERVAL_MS = 1500L
        private const val MOVEMENT_THRESHOLD = 0.2
        private const val STILLNESS_DURATION_MS = 1200L
        private const val MINDFUL_DURATION_MS = 5000L
    }

    private val scope = CoroutineScope(
        Dispatchers.Default
    )

    private var detectionJob: Job? = null
    private var intentionalityJob: Job? = null

    private var lastDetectionTime = System.currentTimeMillis()

    private var pickupStartTime: Long? = null
    private var lastMovementTime: Long? = null

    private var isCurrentlyPickedUp = false

    private var lastX = 0.0
    private var lastY = 0.0
    private var lastZ = 0.0

    private val _onPhoneCheckDetected =
        MutableSharedFlow<Boolean>(
            extraBufferCapacity = 1
        )

    val onPhoneCheckDetected: Flow<Boolean> =
        _onPhoneCheckDetected.asSharedFlow()

    private val _onStatsUpdated =
        MutableSharedFlow<Unit>(
            extraBufferCapacity = 1
        )

    val onStatsUpdated: Flow<Unit> =
        _onStatsUpdated.asSharedFlow()

    fun startDetection() {
        if (detectionJob?.isActive == true) {
            return
        }

        detectionJob = scope.launch {
            sensorDataSource.accelerationFlow.collect { acceleration ->
                processAcceleration(acceleration)
            }
        }
    }

    private suspend fun processAcceleration(
        acceleration: Acceleration
    ) {
        val now = System.currentTimeMillis()

        val x = acceleration.x.toDouble()
        val y = acceleration.y.toDouble()
        val z = acceleration.z.toDouble()

        val magnitude = sqrt(
            x.pow(2) +
                    y.pow(2) +
                    z.pow(2)
        )

        val deltaX = abs(x - lastX)
        val deltaY = abs(y - lastY)
        val deltaZ = abs(z - lastZ)

        val totalDelta =
            deltaX + deltaY + deltaZ

        lastX = x
        lastY = y
        lastZ = z

        if (magnitude > threshold) {

            if (
                now - lastDetectionTime >=
                MIN_DETECTION_INTERVAL_MS
            ) {
                lastDetectionTime = now

                pickupStartTime = now
                lastMovementTime = now
                isCurrentlyPickedUp = true

                repository.savePickup(now)

                _onPhoneCheckDetected.tryEmit(true)

                startIntentionalityTimer()
            }

            return
        }

        if (
            isCurrentlyPickedUp &&
            pickupStartTime != null
        ) {

            if (totalDelta > MOVEMENT_THRESHOLD) {
                lastMovementTime = now
            }

            val lastMovement = lastMovementTime

            if (
                lastMovement != null &&
                now - lastMovement >= STILLNESS_DURATION_MS
            ) {

                val secondsElapsed =
                    (now - pickupStartTime!!) / 1000

                if (secondsElapsed < 5) {

                    intentionalityJob?.cancel()

                    isCurrentlyPickedUp = false
                    lastMovementTime = null

                    registerUnlockType(
                        isMindful = false
                    )

                    _onStatsUpdated.tryEmit(Unit)
                }
            }
        }
    }

    private fun startIntentionalityTimer() {
        intentionalityJob?.cancel()

        intentionalityJob = scope.launch {

            delay(MINDFUL_DURATION_MS)

            if (isCurrentlyPickedUp) {

                registerUnlockType(
                    isMindful = true
                )

                isCurrentlyPickedUp = false
                lastMovementTime = null

                _onStatsUpdated.tryEmit(Unit)
            }
        }
    }

    private fun registerUnlockType(
        isMindful: Boolean
    ) {
        if (isMindful) {
            repository.incrementMindfulCount()
        } else {
            repository.incrementImpulsiveCount()
        }
    }

    fun getMindfulPercentage(): Int {
        val mindful =
            repository.getMindfulCount()

        val impulsive =
            repository.getImpulsiveCount()

        val total = mindful + impulsive

        if (total == 0) {
            return 0
        }

        return (
                mindful.toDouble() /
                        total.toDouble() *
                        100
                ).roundToInt()
    }

    fun getImpulsivePercentage(): Int {
        val mindful =
            repository.getMindfulCount()

        val impulsive =
            repository.getImpulsiveCount()

        val total = mindful + impulsive

        if (total == 0) {
            return 0
        }

        return (
                impulsive.toDouble() /
                        total.toDouble() *
                        100
                ).roundToInt()
    }

    fun getTodayPickupCount(): Int {
        return repository
            .getTodayPickups()
            .size
    }

    fun getBaselineDifferenceText(): String {
        val currentPickups =
            getTodayPickupCount()

        val difference =
            currentPickups - BASELINE_PICKUPS

        return when {
            difference < 0 ->
                "$difference vs baseline"

            difference > 0 ->
                "+$difference vs baseline"

            else ->
                "Same as baseline"
        }
    }

    fun getAverageIntervalMinutes(): Int {
        val pickups =
            repository.getTodayPickups()

        if (pickups.size < 2) {
            return 0
        }

        val firstPickup = pickups.first()
        val lastPickup = pickups.last()

        val totalMinutes =
            (lastPickup - firstPickup) /
                    60_000.0

        return (
                totalMinutes /
                        (pickups.size - 1)
                ).roundToInt()
    }

    fun stopDetection() {
        detectionJob?.cancel()
        detectionJob = null

        intentionalityJob?.cancel()
        intentionalityJob = null

        isCurrentlyPickedUp = false
    }

    fun dispose() {
        stopDetection()
        scope.coroutineContext.cancel()
    }
}