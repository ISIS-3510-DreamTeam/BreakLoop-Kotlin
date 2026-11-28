package com.dreamteam.breakloop.data.local.cache

import androidx.collection.LruCache
import com.dreamteam.breakloop.domain.Friend
import com.dreamteam.breakloop.domain.FriendProfile

object FriendProfileMemoryCache {

    private val cache = LruCache<String, FriendProfile>(MAX_ENTRIES)

    fun get(uid: String): FriendProfile? {
        val profile = cache[uid] ?: return null
        val now = System.currentTimeMillis()
        if ((now - profile.fetchedAt) > TTL_MS) {
            cache.remove(uid)
            return null
        } else
            return profile
    }

    fun put(profile: FriendProfile) {
        cache.put(profile.uid, profile)
    }

    fun removeIfOutdated(friend: Friend) {
        val profile = cache[friend.uid] ?: return
        if (profile.displayName != friend.displayName ||
            profile.streak != friend.streak ||
            profile.petLevel != friend.petLevel
        ) {
            cache.remove(friend.uid)
        }
    }


        const val MAX_ENTRIES = 20
        const val TTL_MS = 5 * 60 * 1000L
}