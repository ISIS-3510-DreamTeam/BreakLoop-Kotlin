
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
        val conexion = URL("$baseUrl/$nombre").openConnection()
                as HttpURLConnection

        try {
            conexion.connectTimeout = 15000
            conexion.readTimeout = 30000

            if (conexion.responseCode != HttpURLConnection.HTTP_OK) {
                throw IllegalStateException(
                    "No se pudo descargar $nombre: HTTP ${conexion.responseCode}"
                )
            }

            val temporal = File(carpeta, "$nombre.tmp")

            try {
                conexion.inputStream.use { entrada ->
                    temporal.outputStream().use { salida ->
                        entrada.copyTo(salida)
                    }
                }

                if (temporal.length() == 0L) {
                    throw IllegalStateException("El archivo descargado está vacío")
                }

                if (!temporal.renameTo(archivo)) {
                    throw IllegalStateException("No se pudo guardar el sonido $nombre")
                }
            } finally {
                temporal.delete()
            }
        } finally {
            conexion.disconnect()
        }
    }
}
