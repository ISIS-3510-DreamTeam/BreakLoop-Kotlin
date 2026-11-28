package com.dreamteam.breakloop.data.achievement

import android.util.LruCache
import com.dreamteam.breakloop.domain.AchievementProgress

/**
 * In-memory LruCache with the last calculated progress of each achievement,
 * so the detail screen opens without evaluating the rules again.
 *
 * Key: "uid/achievementId", so two accounts on the same phone never mix.
 * maxSize counts entries (sizeOf() returns 1 by default): the catalog has 5 achievements
 * (8 planned), so 32 leaves room for several accounts. Each entry is a few hundred bytes.
 * When it is full, the least recently used entry is evicted.
 */
object AchievementProgressCache {

    private const val MAX_ENTRIES = 32

    private val cache = LruCache<String, AchievementProgress>(MAX_ENTRIES)

    private fun key(uid: String, achievementId: String) = "$uid/$achievementId"

    fun put(uid: String, progress: AchievementProgress) {
        cache.put(key(uid, progress.achievement.id), progress)
    }

    fun get(uid: String, achievementId: String): AchievementProgress? =
        cache.get(key(uid, achievementId))
}