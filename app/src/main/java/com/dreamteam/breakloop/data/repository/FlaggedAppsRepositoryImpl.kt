package com.dreamteam.breakloop.data.repository

import com.dreamteam.breakloop.data.local.dao.FlaggedAppDao
import com.dreamteam.breakloop.data.local.mapper.toDomain
import com.dreamteam.breakloop.domain.FlaggedApp
import com.dreamteam.breakloop.domain.repository.FlaggedAppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FlaggedAppsRepositoryImpl(
    private val flaggedAppsDao: FlaggedAppDao
) : FlaggedAppRepository{

    override suspend fun addFlaggedApp(packageName: String, limit: Long?) {
         flaggedAppsDao.insertOrReplaceApp(packageName, limit, System.currentTimeMillis())
    }

    override suspend fun changeFlaggedAppLimit(packageName: String, limit: Long) {
        flaggedAppsDao.updateLimit(packageName, limit)
    }

    override suspend fun deleteUninstalledApps(packages: Set<String>) {
        flaggedAppsDao.deleteUninstalled(packages)
    }

    override suspend fun getAllApps(): List<String> {
        return flaggedAppsDao.getAllPackages()
    }

    suspend fun deletePackage(packageName: String) {
        flaggedAppsDao.deletePackage(packageName)
    }

    // observemoslas
    override fun getFlaggedApps(): Flow<List<FlaggedApp>> {
        return flaggedAppsDao.observeFlaggedApps().map {
            list -> list.map { it -> it.toDomain() }
        }
    }

}