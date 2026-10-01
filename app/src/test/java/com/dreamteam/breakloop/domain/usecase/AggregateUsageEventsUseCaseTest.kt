package com.dreamteam.breakloop.domain.usecase

import com.dreamteam.breakloop.domain.AppUsage
import com.dreamteam.breakloop.domain.UsageEvent
import com.dreamteam.breakloop.domain.enums.UsageEventType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AggregateUsageEventsUseCaseTest {

    private lateinit var useCase: AggregateUsageEventsUseCase

    private val rangeStart = 0L
    private val rangeEnd = 10000L
    private val date = "2026-10-01"
    private val includedPackages = setOf("A", "B")

    @Before
    fun setUp() {
        useCase = AggregateUsageEventsUseCase()
    }

    private fun executeUseCase(events: List<UsageEvent>): Map<String, Long> {
        val result: List<AppUsage> = useCase.getScreenTimePerPackage(
            usageEvents = events,
            rangeStart = rangeStart,
            rangeEnd = rangeEnd,
            date = date,
            includedPackages = includedPackages
        )
        return result.associate { it.packageName to it.foregroundMs }
    }

    @Test
    fun `1 Sesion simple - A entra 1000, A sale 5000`() {
        val events = listOf(
            UsageEvent("A", 1000, UsageEventType.FOREGROUND),
            UsageEvent("A", 5000, UsageEventType.BACKGROUND)
        )

        val result = executeUseCase(events)

        assertEquals(4000L, result["A"])
    }

    @Test
    fun `2 Venia de antes - A sale 3000`() {
        val events = listOf(
            UsageEvent("A", 3000, UsageEventType.BACKGROUND)
        )

        val result = executeUseCase(events)

        assertEquals(3000L, result["A"])
    }

    @Test
    fun `3 Queda abierta - A entra 7000`() {
        val events = listOf(
            UsageEvent("A", 7000, UsageEventType.FOREGROUND)
        )

        val result = executeUseCase(events)

        assertEquals(3000L, result["A"])
    }

    @Test
    fun `4 Solapamiento - B entra 100, A entra 200, B sale 300, A sale 500`() {
        val events = listOf(
            UsageEvent("B", 100, UsageEventType.FOREGROUND),
            UsageEvent("A", 200, UsageEventType.FOREGROUND),
            UsageEvent("B", 300, UsageEventType.BACKGROUND),
            UsageEvent("A", 500, UsageEventType.BACKGROUND)
        )

        val result = executeUseCase(events)

        assertEquals(300L, result["A"])
        assertEquals(200L, result["B"])
    }

    @Test
    fun `5 Excluida - C entra 1000, C sale 2000`() {
        val events = listOf(
            UsageEvent("C", 1000, UsageEventType.FOREGROUND),
            UsageEvent("C", 2000, UsageEventType.BACKGROUND)
        )

        val result = executeUseCase(events)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `6 Pantalla apagada - A entra 1000, pantalla 4000`() {
        val events = listOf(
            UsageEvent("A", 1000, UsageEventType.FOREGROUND),
            UsageEvent("", 4000, UsageEventType.SCREEN_OFF)
        )

        val result = executeUseCase(events)

        assertEquals(3000L, result["A"])
    }

    @Test
    fun `7 BACKGROUND despues de apagar - A entra 1000, pantalla 4000, A sale 4050`() {
        val events = listOf(
            UsageEvent("A", 1000, UsageEventType.FOREGROUND),
            UsageEvent("", 4000, UsageEventType.SCREEN_OFF),
            UsageEvent("A", 4050, UsageEventType.BACKGROUND)
        )

        val result = executeUseCase(events)

        assertEquals(3000L, result["A"])
    }

    @Test
    fun `8 Dos pantallas internas - A entra 1000, A entra 2000, A sale 2100, A sale 5000`() {
        val events = listOf(
            UsageEvent("A", 1000, UsageEventType.FOREGROUND),
            UsageEvent("A", 2000, UsageEventType.FOREGROUND),
            UsageEvent("A", 2100, UsageEventType.BACKGROUND),
            UsageEvent("A", 5000, UsageEventType.BACKGROUND)
        )

        val result = executeUseCase(events)

        assertEquals(1100L, result["A"])
    }

    @Test
    fun `9 Lista vacia - ninguno`() {
        val events = emptyList<UsageEvent>()

        val result = executeUseCase(events)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `10 Desordenados - A sale 5000, A entra 1000 (desordenados en lista)`() {
        val events = listOf(
            UsageEvent("A", 5000, UsageEventType.BACKGROUND),
            UsageEvent("A", 1000, UsageEventType.FOREGROUND)
        )

        val result = executeUseCase(events)

        assertEquals(4000L, result["A"])
    }
}
