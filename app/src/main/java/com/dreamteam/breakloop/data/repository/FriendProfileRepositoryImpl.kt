package com.dreamteam.breakloop.data.repository

import com.dreamteam.breakloop.data.local.cache.FriendProfileMemoryCache
import com.dreamteam.breakloop.data.local.dao.FriendProfileDao
import com.dreamteam.breakloop.data.local.mapper.toDomain
import com.dreamteam.breakloop.data.local.mapper.toEntity
import com.dreamteam.breakloop.data.remote.ProfileRemoteDataSource
import com.dreamteam.breakloop.data.system.ConnectivityDataSource
import com.dreamteam.breakloop.domain.FriendProfileResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class FriendProfileRepositoryImpl (
    private val remote: ProfileRemoteDataSource,
    private val dao: FriendProfileDao,
    private val memoryCache: FriendProfileMemoryCache,
    private val connectivity: ConnectivityDataSource
) {
    suspend fun getFriendProfile(friendUid: String): FriendProfileResult {
        memoryCache.get(friendUid)?.let { friend -> return FriendProfileResult.Fresh(friend)}

        if (!connectivity.isOnline()) return fromRoom(friendUid)

        return try {
            val profile = withContext(Dispatchers.IO) {
                coroutineScope {
                    val publicDeferred = async {
                        remote.getPublicProfile(friendUid)
                    }
                    val badgesDeferred = async {
                        remote.getBadges(friendUid)
                    }
                    publicDeferred.await().toDomain(
                        badges = badgesDeferred.await(),
                        fetchedAt = System.currentTimeMillis()
                    )
                }
            }
            dao.upsert(profile.toEntity())
            memoryCache.put(profile)
            FriendProfileResult.Fresh(profile)
        } catch (e: IOException){
            fromRoom(friendUid)
        }
        catch (e: HttpException){
            if (e.code() == 404) FriendProfileResult.NotFound else fromRoom(friendUid)
        }
    }

    private suspend fun fromRoom(friendUid: String): FriendProfileResult{
        val entity = dao.getFriendProfileByUid(friendUid) ?: return FriendProfileResult.NeverLoaded
        return FriendProfileResult.Stale(entity.toDomain())
    }
}