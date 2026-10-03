package com.dreamteam.breakloop

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.dreamteam.breakloop.background.worker.UsageWorkScheduler
import com.dreamteam.breakloop.data.remote.WeatherApiFactory
import com.dreamteam.breakloop.data.system.LocationDataSource
import com.dreamteam.breakloop.data.system.PermissionsDataSource
import com.dreamteam.breakloop.domain.enums.WeatherCondition
import com.dreamteam.breakloop.ui.theme.BreakLoopTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        UsageWorkScheduler.schedule(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            val permissionsDataSource = PermissionsDataSource(applicationContext)
            val locationDataSource = LocationDataSource(applicationContext, permissionsDataSource)

            val coords = locationDataSource.getApproximateLocation()
            Log.d("LocationTest", "Coordenadas obtenidas: $coords")

            if (coords != null) {
                try {
                    val weatherApi = WeatherApiFactory.create()
                    val response = weatherApi.getCurrentWeather(coords.latitude, coords.longitude)
                    val code = response.current.weatherCode
                    val condition = WeatherCondition.fromWmoCode(code)
                    Log.d("WeatherTest", "Código WMO: $code -> Condición: $condition")
                } catch (e: Exception) {
                    Log.e("WeatherTest", "Error al obtener el clima", e)
                }
            } else {
                Log.w("LocationTest", "No se obtuvo ubicación (verifica permisos y configuración de ubicación del emulador)")
            }
        }

        setContent {
            val navController = rememberNavController()
            BreakLoopTheme {
                BreakLoopApp()
            }
        }
    }
}
// TODO: acá debe vivir el splash
