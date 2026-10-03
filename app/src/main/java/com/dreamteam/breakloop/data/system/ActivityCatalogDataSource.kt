package com.dreamteam.breakloop.data.system

import android.content.Context
import com.dreamteam.breakloop.domain.OfflineActivity
import com.dreamteam.breakloop.domain.enums.ActivityCategory
import com.dreamteam.breakloop.domain.enums.TimeOfDay
import com.dreamteam.breakloop.domain.enums.WeatherCondition
import org.json.JSONArray

class ActivityCatalogDataSource(
    private val context: Context
) {
    fun loadBundledCatalog(): List<OfflineActivity> {
        val text = context.assets.open("activities.json").bufferedReader().use { it.readText() }
        val raw = JSONArray(text)
        val activities = mutableListOf<OfflineActivity>()

        for (i in 0 until raw.length()) {
            val instance = raw.getJSONObject(i)

            val weatherArray = instance.getJSONArray("weather")
            val suitableWeather = (0 until weatherArray.length()).mapTo(mutableSetOf()) { j ->
                WeatherCondition.valueOf(weatherArray.getString(j))
            }

            val timeOfDayArray = instance.getJSONArray("timeOfDay")
            val suitableTimesOfDay = (0 until timeOfDayArray.length()).mapTo(mutableSetOf()) { j ->
                TimeOfDay.valueOf(timeOfDayArray.getString(j))
            }

            val activity = OfflineActivity(
                id = instance.getString("id"),
                title = instance.getString("title"),
                category = ActivityCategory.valueOf(instance.getString("category")),
                durationMin = instance.getInt("durationMin"),
                xp = instance.getInt("xp"),
                prompt = instance.getString("prompt"),
                isOutdoor = instance.getBoolean("outdoor"),
                suitableWeather = suitableWeather,
                suitableTimesOfDay = suitableTimesOfDay
            )
            activities.add(activity)
        }
        return activities
    }
}