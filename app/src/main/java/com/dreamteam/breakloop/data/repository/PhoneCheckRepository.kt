package com.dreamteam.breakloop.data.local.repository

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class PhoneCheckRepository(
    context: Context
) {
    //Declarar constantes que no necesitan instancia específica- dónde se va a almacenar info
    companion object {
        private const val PREFS_NAME =
            "phone_check_preferences"

        private const val KEY_PICKUPS =
            "phone_check_timestamps"

        private const val KEY_MINDFUL_COUNT =
            "mindful_count"

        private const val KEY_IMPULSIVE_COUNT =
            "impulsive_count"
    }

    private val preferences: SharedPreferences =
        //Conexión con almacenamiento local
        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    //JSONArray-> String ->SharedPreferences->AlmacenamientoLocal
    fun savePickup(timestamp: Long) {

        val pickups = getPickups().toMutableList()

        pickups.add(timestamp)

        val jsonArray = JSONArray()

        pickups.forEach { pickup ->
            jsonArray.put(pickup)
        }
        //sharedPreferences no funciona bien con list<long>, se requiere conversión
        //Key_PICKUPS es un string
        preferences.edit()
            .putString(
                KEY_PICKUPS,
                jsonArray.toString()
            )
            .apply()
    }

    //Get el string almacenado: busca phone_check_timestamps
    fun getPickups(): List<Long> {

        val stored =
            preferences.getString(
                KEY_PICKUPS,
                null
            ) ?: return emptyList() //Si stored es null devuelve lista vacia

        val jsonArray =
            JSONArray(stored)

        return List(jsonArray.length()) { index ->
            jsonArray.getLong(index)
        }
    }

    fun getTodayPickups(): List<Long> {

        val today =
            LocalDate.now()

        return getPickups().filter { timestamp ->

            val date =
                Instant
                    .ofEpochMilli(timestamp)
                    .atZone(
                        ZoneId.systemDefault()
                    )
                    .toLocalDate()

            date == today
        }
    }

    //ELiminar los que no sean de hoy
        fun clearOldPickups() {

        val todayPickups =
            getTodayPickups()

        val jsonArray =
            JSONArray()

        todayPickups.forEach { pickup ->
            jsonArray.put(pickup)
        }
        //Sobreescribe los datos anteriores
        preferences.edit()
            .putString(
                KEY_PICKUPS,
                jsonArray.toString()
            )
            .apply()
    }
    // Obtiene la cantidad actual de pickups impulsivos
    // Si todavía no existe un valor almacenado, devuelve 0
    fun getMindfulCount(): Int {
        return preferences.getInt(
            KEY_MINDFUL_COUNT,
            0
        )
    }


    fun getImpulsiveCount(): Int {
        return preferences.getInt(
            KEY_IMPULSIVE_COUNT,
            0
        )
    }

    // Incrementa en uno la cantidad de pickups conscientes
    fun incrementMindfulCount() {

        val current =
            getMindfulCount()

        preferences.edit()
            .putInt(
                KEY_MINDFUL_COUNT,
                current + 1
            )
            .apply()
    }

    fun incrementImpulsiveCount() {

        val current =
            getImpulsiveCount()

        preferences.edit()
            .putInt(
                KEY_IMPULSIVE_COUNT,
                current + 1
            )
            .apply()
    }
}