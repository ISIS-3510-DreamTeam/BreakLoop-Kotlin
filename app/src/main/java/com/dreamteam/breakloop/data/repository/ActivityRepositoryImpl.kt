package com.dreamteam.breakloop.data.repository

import com.dreamteam.breakloop.data.system.ActivityCatalogDataSource
import com.dreamteam.breakloop.domain.OfflineActivity
import com.dreamteam.breakloop.domain.repository.ActivityRepository

class ActivityRepositoryImpl(
    private val catalogDataSource: ActivityCatalogDataSource
): ActivityRepository {
    private var cache: List<OfflineActivity>? = null

    override fun getCatalog(): List<OfflineActivity> {
        if (cache == null) {
            cache = catalogDataSource.loadBundledCatalog()
        }
        return cache ?: emptyList()
    }

    override fun getActivity(id: String): OfflineActivity? {
        return getCatalog().firstOrNull { it.id == id }
    }
}