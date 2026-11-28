package com.dreamteam.breakloop.ui.main.achievements.components

import android.content.Context
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import okio.Path.Companion.toOkioPath

/**
 * Coil ImageLoader only for the medals, with its own caches:
 * - Memory cache (LRU): 8 MB. The medals are small PNGs, so it holds all of them decoded.
 * - Disk cache: 20 MB in cacheDir/achievement_icons, so a medal downloaded once
 *   still appears offline. The system may clear cacheDir if it runs low on space.
 * One instance for the whole app: creating an ImageLoader is expensive and the caches must be shared.
 */
object AchievementImageLoader {

    private const val MEMORY_CACHE_BYTES = 8L * 1024 * 1024
    private const val DISK_CACHE_BYTES = 20L * 1024 * 1024

    @Volatile
    private var instance: ImageLoader? = null

    fun get(context: Context): ImageLoader =
        instance ?: synchronized(this) {
            instance ?: build(context.applicationContext).also { instance = it }
        }

    private fun build(context: Context): ImageLoader =
        ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizeBytes(MEMORY_CACHE_BYTES)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("achievement_icons").toOkioPath())
                    .maxSizeBytes(DISK_CACHE_BYTES)
                    .build()
            }
            .build()
}