
package com.dreamteam.breakloop.data.local.cache

import android.content.Context
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class SoundscapeCache(context: Context) {

    private val carpeta = File(context.filesDir, "soundscapes")

    private val baseUrl =
        "https://breakloop-backend-14746672383.southamerica-west1.run.app/api/soundscapes"

    init {
        carpeta.mkdirs()
    }

    fun obtenerSonido(nombre: String): File {
        require(nombre.isNotBlank()) {
            "El nombre del sonido no puede estar vacío"
        }

        val archivo = File(carpeta, "$nombre.mp3")

        //Revisa primero si esta disponbible en el almacenamiento local
        if (archivo.exists() && archivo.length() > 0) {
            return archivo
        }

        descargarSonido(nombre, archivo)
        return archivo
    }

    private fun descargarSonido(nombre: String, archivo: File) {
        val temporal = File(carpeta, "$nombre.tmp")
        val url = URL("$baseUrl/$nombre")

        //Petición- Recibe bytes
        val flujoEntrada = url.openStream()
        val flujoSalida = temporal.outputStream()

        try {
            //Bytes a local
            flujoEntrada.copyTo(flujoSalida)
        } finally {
            flujoEntrada.close()
            flujoSalida.close()
        }

        if (temporal.length() == 0L) {
            temporal.delete()
            throw IllegalStateException("El audio descargado está vacío")
        }

        if (!temporal.renameTo(archivo)) {
            temporal.delete()
            throw IllegalStateException("No se pudo guardar el audio")
        }
    }
}
