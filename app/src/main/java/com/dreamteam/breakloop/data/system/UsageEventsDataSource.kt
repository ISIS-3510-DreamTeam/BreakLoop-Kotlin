package com.dreamteam.breakloop.data.system

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import com.dreamteam.breakloop.domain.UsageEvent
import com.dreamteam.breakloop.domain.UsageEventsResult
import com.dreamteam.breakloop.domain.enums.UsageEventType

//Su único trabajo es: pedirle a Android los eventos de un rango de tiempo y devolverlos como una lista de tus UsageEvent
// importante: para esta vaina necesito permisos, sino me salen en 0 los eventos
class UsageEventsDataSource (
    private val context: Context,
    private val permissionsDataSource: PermissionsDataSource
){
    fun obtainEvents(startTime: Long, endTime: Long): UsageEventsResult {
        if (!permissionsDataSource.hasUsageAccess()){
            return UsageEventsResult.NoPermission
        }
        val events = mutableListOf<UsageEvent>()
        val usageStatsManager: UsageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val eventsRaw = usageStatsManager.queryEvents(startTime, endTime)
        val event: UsageEvents.Event = UsageEvents.Event()
        while (eventsRaw.hasNextEvent()) {
            eventsRaw.getNextEvent(event)

            val eventType: UsageEventType? = when (event.eventType) {
                UsageEvents.Event.ACTIVITY_RESUMED -> UsageEventType.FOREGROUND
                UsageEvents.Event.ACTIVITY_PAUSED -> UsageEventType.BACKGROUND
                UsageEvents.Event.SCREEN_NON_INTERACTIVE -> UsageEventType.SCREEN_OFF
                else -> null
            }
            if (eventType!=null){
                events.add(UsageEvent(event.packageName, event.timeStamp, eventType))
            }
        }
        return UsageEventsResult.Success(events)
    }
}