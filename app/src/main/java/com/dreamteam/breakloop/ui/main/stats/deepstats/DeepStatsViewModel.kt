package com.dreamteam.breakloop.ui.main.stats.deepstats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dreamteam.breakloop.data.local.repository.PhoneCheckRepository
import com.dreamteam.breakloop.data.tracking.PhoneCheckService
import com.dreamteam.breakloop.data.tracking.SensorDataSource
import com.dreamteam.breakloop.data.tracking.AndroidSensorDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DeepStatsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = PhoneCheckRepository(application)

    private val sensorDataSource = AndroidSensorDataSource(application)

    private val phoneCheckService = PhoneCheckService(
        sensorDataSource = sensorDataSource,
        repository = repository
    )

    private val _uiState =
        MutableStateFlow<DeepStatsUiState>(DeepStatsUiState.Loading)

    val uiState: StateFlow<DeepStatsUiState> =
        _uiState.asStateFlow()

    init {
        loadData()
        startListening()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                _uiState.value = DeepStatsUiState.Content(
                    dailyPickups = phoneCheckService.getTodayPickupCount(),
                    avgIntervalMinutes = phoneCheckService.getAverageIntervalMinutes(),
                    baselineText = phoneCheckService.getBaselineDifferenceText(),
                    mindfulPercentage = phoneCheckService.getMindfulPercentage(),
                    impulsivePercentage = phoneCheckService.getImpulsivePercentage()
                )
            } catch (e: Exception) {
                _uiState.value = DeepStatsUiState.Error(
                    e.message ?: "Unable to load statistics"
                )
            }
        }
    }

    private fun startListening() {
        phoneCheckService.startDetection()

        viewModelScope.launch {
            phoneCheckService.onPhoneCheckDetected.collect {
                loadData()
            }
        }

        viewModelScope.launch {
            phoneCheckService.onStatsUpdated.collect {
                loadData()
            }
        }
    }

    override fun onCleared() {
        phoneCheckService.dispose()
        super.onCleared()
    }
}