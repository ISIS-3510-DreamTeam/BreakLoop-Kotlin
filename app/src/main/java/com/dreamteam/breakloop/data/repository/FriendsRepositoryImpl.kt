package com.dreamteam.breakloop.data.repository

import com.dreamteam.breakloop.data.local.mapper.toDomain
import com.dreamteam.breakloop.data.remote.FriendsRemoteDataSource
import com.dreamteam.breakloop.data.system.ConnectivityDataSource
import com.dreamteam.breakloop.data.system.FriendsLocalDataSource
import com.dreamteam.breakloop.domain.Friend
import com.dreamteam.breakloop.domain.UserProfile
import com.dreamteam.breakloop.domain.enums.AddFriendResult
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException
import java.io.IOException

class FriendsRepositoryImpl(
    private val remoteDataSource: FriendsRemoteDataSource,
    private val localDataSource: FriendsLocalDataSource,
    private val connectivity: ConnectivityDataSource
) {
    private val mutex = Mutex()
    private var cache: List<Friend>?=null
    private var cacheOwner: String ? = null
    private var cachedAt: Long? = null
    private val CACHE_MS = 5 * 60 * 1000L

    fun isOnline() = connectivity.isOnline()
    fun  observeConnection() = connectivity.observeConnection()

    suspend fun ensureProfile(
        uid: String
    ): UserProfile? {
        localDataSource.getProfile(uid)?.let {
            return it.toDomain()
        }
        if (!isOnline()){
            return null
        }
        val dto = remoteDataSource.postFriendProfile()
        localDataSource.saveProfile(uid,dto)
        return dto.toDomain()
    }

    suspend fun getStoredFriends( uid: String): Pair<List<Friend>?, Long?>? {
        mutex.withLock {
            if (cacheOwner ==uid && cache!=null) {
                return Pair(cache,cachedAt)
            }
        }
        val dtos = localDataSource.getFriends(uid)?: return null
        val at = localDataSource.getLastUpdated(uid)?: return null
        val friends = dtos.map { it.toDomain() }
        mutex.withLock {
            cache = friends
            cacheOwner = uid
            cachedAt = at
        }
        return Pair(friends,at)
    }
    suspend fun isCacheFresh(uid: String): Boolean {
        mutex.withLock {
            return cacheOwner == uid && cachedAt != null && System.currentTimeMillis() - cachedAt!! < CACHE_MS
        }
    }

    suspend fun refreshFriends( uid: String): List<Friend> {
        val dtos = remoteDataSource.getFriends()
        val now = System.currentTimeMillis()
        localDataSource.saveFriends(uid,dtos,now)

        val friends = dtos.map { it.toDomain() }

        mutex.withLock {
            cache = friends
            cacheOwner = uid
            cachedAt = now
        }
        return friends
    }

    suspend fun addFriend(code: String): AddFriendResult {
        val dto = remoteDataSource.addFriend(code.trim().uppercase())
        return runCatching { AddFriendResult.valueOf(dto.result) }.getOrElse { AddFriendResult.NOT_FOUND }
    }

    suspend fun syncPendingCodes(uid: String): List<String> {
        val pending = localDataSource.getPendingCodes(uid)
        val quedan = mutableSetOf<String>()
        val invalidos = mutableListOf<String>()

        for (code in pending) {
            try {
                val result = addFriend(code)
                if (result != AddFriendResult.ADDED) {
                    invalidos.add(code)
                }
            } catch (_: IOException) {
                quedan.add(code)
            } catch (_: HttpException) {
                quedan.add(code)
            }
        }

        localDataSource.savePendingCodes(uid, quedan)
        return invalidos
    }

    suspend fun queuePendingCode(uid: String, code: String ): Set<String> {
        val pending = localDataSource.getPendingCodes(uid)
        localDataSource.savePendingCodes(uid, pending)
        return pending
    }

}