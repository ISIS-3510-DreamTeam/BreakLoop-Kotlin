package com.dreamteam.breakloop.data.tracking

import kotlinx.coroutines.flow.Flow

data class Acceleration(
    val x: Float,
    val y: Float,
    val z: Float
)

interface SensorDataSource {

    val accelerationFlow: Flow<Acceleration>
}