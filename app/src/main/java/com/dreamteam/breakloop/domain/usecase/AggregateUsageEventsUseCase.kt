package com.dreamteam.breakloop.domain.usecase

import com.dreamteam.breakloop.domain.AppUsage
import com.dreamteam.breakloop.domain.UsageEvent
import com.dreamteam.breakloop.domain.enums.UsageEventType

class AggregateUsageEventsUseCase {
    fun getScreenTimePerPackage(
        usageEvents: List<UsageEvent>,
        rangeStart: Long,
        rangeEnd: Long,
        date: String,
        includedPackages: Set<String>
    ): List<AppUsage> {
        val openPackages: MutableMap<String, Long> = mutableMapOf()
        val allPackages: MutableMap<String, Long> = mutableMapOf()
        val seenPackages: MutableSet<String> = mutableSetOf()
        var screenOffOccurred = false

        val sortedEvents = usageEvents.sortedBy { it.timestamp }

        for (event in sortedEvents) {
            if (event.type == UsageEventType.SCREEN_OFF) {
                for ((packageName, startTime) in openPackages) {
                    if (includedPackages.contains(packageName)) {
                        val current = allPackages[packageName] ?: 0L
                        val inc = event.timestamp - startTime
                        allPackages[packageName] = current + inc
                    }
                }
                openPackages.clear()
                screenOffOccurred = true
                continue
            }

            if (!includedPackages.contains(event.packageName)) {
                continue
            }

            if (event.type == UsageEventType.FOREGROUND) {
                if (!openPackages.containsKey(event.packageName)) {
                    openPackages[event.packageName] = event.timestamp
                }
                seenPackages.add(event.packageName)
            } else if (event.type == UsageEventType.BACKGROUND) {
                val start = openPackages[event.packageName]

                if (start != null) {
                    val current = allPackages[event.packageName] ?: 0L
                    allPackages[event.packageName] = current + (event.timestamp - start)
                    openPackages.remove(event.packageName)
                } else {
                    if (!seenPackages.contains(event.packageName) && !screenOffOccurred) {
                        val current = allPackages[event.packageName] ?: 0L
                        allPackages[event.packageName] = current + (event.timestamp - rangeStart)
                    }
                }
                seenPackages.add(event.packageName)
            }
        }

        for ((packageName, startTime) in openPackages) {
            val current = allPackages[packageName] ?: 0L
            allPackages[packageName] = current + (rangeEnd - startTime)
        }
        val listPackages: MutableList<AppUsage> = mutableListOf()
        for ((packageName, foregroundMs) in allPackages) {
            listPackages.add(AppUsage(date, packageName, foregroundMs))
        }

        return listPackages
    }
}
