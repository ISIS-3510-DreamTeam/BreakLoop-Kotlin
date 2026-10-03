package com.dreamteam.breakloop.data.tracking

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AndroidSensorDataSource(
    context: Context
) : SensorDataSource {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accelerometer =
        sensorManager.getDefaultSensor(
            Sensor.TYPE_ACCELEROMETER
        )

    override val accelerationFlow: Flow<Acceleration> =
        callbackFlow {

            if (accelerometer == null) {
                close(
                    IllegalStateException(
                        "Accelerometer is not available"
                    )
                )
                return@callbackFlow
            }

            val listener = object : SensorEventListener {

                override fun onSensorChanged(
                    event: SensorEvent
                ) {
                    if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                        trySend(
                            Acceleration(
                                x = event.values[0],
                                y = event.values[1],
                                z = event.values[2]
                            )
                        )
                    }
                }

                override fun onAccuracyChanged(
                    sensor: Sensor?,
                    accuracy: Int
                ) {
                }
            }

            sensorManager.registerListener(
                listener,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )

            awaitClose {
                sensorManager.unregisterListener(listener)
            }
        }
}