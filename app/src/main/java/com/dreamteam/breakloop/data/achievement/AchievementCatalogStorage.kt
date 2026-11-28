package com.dreamteam.breakloop.data.achievement

import android.content.Context
import java.io.File

/**
 * Local file with the last catalog received from the backend (filesDir/achievements_catalog.json),
 * plus the copy bundled in assets for a first launch without network.
 */
class AchievementCatalogStorage(private val context: Context) {

    private val file = File(context.filesDir, "achievements_catalog.json")
    private val tempFile = File(context.filesDir, "achievements_catalog.json.tmp")

    /** Writes to a temporary file first, so an interrupted write never leaves half a catalog. */
    fun save(json: String) {
        tempFile.writeText(json)
        if (!tempFile.renameTo(file)) {
            file.writeText(json)
            tempFile.delete()
        }
    }

    fun read(): String? = if (file.exists()) file.readText() else null

    fun readBundled(): String =
        context.assets.open("achievements.json").bufferedReader().use { it.readText() }
}