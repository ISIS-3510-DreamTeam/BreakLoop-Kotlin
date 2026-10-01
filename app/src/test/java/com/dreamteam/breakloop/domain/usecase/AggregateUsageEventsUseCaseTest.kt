package com.dreamteam.breakloop.domain.usecase

import com.dreamteam.breakloop.domain.UsageEvent
import com.dreamteam.breakloop.domain.enums.UsageEventType
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AggregateUsageEventsUseCaseTest {

    private lateinit var useCase: AggregateUsageEventsUseCase

    @Before
    fun setUp() {
        useCase = AggregateUsageEventsUseCase()
    }

    @Test
    fun `Caso 1 - BACKGROUND despues de SCREEN_OFF es ignorado`() {
        // App A entra a los 1000 ms
        // La pantalla se apaga a los 4000 ms -> App A suma 3000 ms y se limpia openPackages
        // App A sale (BACKGROUND) a los 4050 ms -> Debe ser ignorado, no debe sumar desde rangeStart=0
        val events = listOf(
            UsageEvent("com.app.a", 1000, UsageEventType.FOREGROUND),
            UsageEvent("", 4000, UsageEventType.SCREEN_OFF),
            UsageEvent("com.app.a", 4050, UsageEventType.BACKGROUND)
        )

        val result = useCase.getScreenTimePerPackage(
            usageEvents = events,
            rangeStart = 0L,
            rangeEnd = 10000L,
            date = "2026-10-01",
            includedPackages = setOf("com.app.a")
        )

        // Debe sumar exactamente 3000 ms (4000 - 1000), ignorando el BACKGROUND de las 4050 ms.
        assertEquals(3000L, result["com.app.a"])
    }

    @Test
    fun `Caso 2 - Actividades superpuestas de una misma app ignora el segundo BACKGROUND`() {
        // App A (Pantalla 1) entra a los 1000 ms
        // App A (Pantalla 2) entra a los 2000 ms -> ya en openPackages, se ignora
        // App A (Pantalla 1) sale a los 2100 ms -> suma 1100 ms (2100 - 1000), remueve de openPackages
        // App A (Pantalla 2) sale a los 5000 ms -> ya no esta en openPackages y ya fue vista -> se ignora
        val events = listOf(
            UsageEvent("com.app.a", 1000, UsageEventType.FOREGROUND),
            UsageEvent("com.app.a", 2000, UsageEventType.FOREGROUND),
            UsageEvent("com.app.a", 2100, UsageEventType.BACKGROUND),
            UsageEvent("com.app.a", 5000, UsageEventType.BACKGROUND)
        )

        val result = useCase.getScreenTimePerPackage(
            usageEvents = events,
            rangeStart = 0L,
            rangeEnd = 10000L,
            date = "2026-10-01",
            includedPackages = setOf("com.app.a")
        )

        // Debe sumar 1100 ms (2100 - 1000), ignorando el segundo BACKGROUND.
        assertEquals(1100L, result["com.app.a"])
    }

    @Test
    fun `Caso 3 - App abierta antes de medianoche aplica regla de rangeStart`() {
        // La app estaba abierta antes de rangeStart=0.
        // Llega un BACKGROUND a los 1000 ms sin FOREGROUND previo, sin SCREEN_OFF y sin haber visto la app antes.
        val events = listOf(
            UsageEvent("com.app.a", 1000, UsageEventType.BACKGROUND)
        )

        val result = useCase.getScreenTimePerPackage(
            usageEvents = events,
            rangeStart = 0L,
            rangeEnd = 10000L,
            date = "2026-10-01",
            includedPackages = setOf("com.app.a")
        )

        // Aplica la regla rangeStart: suma 1000 ms (1000 - 0).
        assertEquals(1000L, result["com.app.a"])
    }
}
