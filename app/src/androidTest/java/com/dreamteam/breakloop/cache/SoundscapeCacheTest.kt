package com.dreamteam.breakloop.cache

import androidx.test.platform.app.InstrumentationRegistry
import com.dreamteam.breakloop.data.local.cache.SoundscapeCache
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SoundscapeCacheTest {

    @Test
    fun probarDescargaYCache() {
        val contexto =
            InstrumentationRegistry.getInstrumentation().targetContext

        val archivoLocal = java.io.File(
            contexto.filesDir,
            "soundscapes/rain.mp3"
        )

        archivoLocal.delete()

        val cache = SoundscapeCache(contexto)

        val primerArchivo = cache.obtenerSonido("rain")

        assertTrue(primerArchivo.exists())
        assertTrue(primerArchivo.length() > 0)

        val fechaModificacion = primerArchivo.lastModified()

        val segundoArchivo = cache.obtenerSonido("rain")

        assertEquals(
            primerArchivo.absolutePath,
            segundoArchivo.absolutePath
        )

        assertEquals(
            fechaModificacion,
            segundoArchivo.lastModified()
        )
    }
}