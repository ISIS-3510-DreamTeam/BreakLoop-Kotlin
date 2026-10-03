package com.dreamteam.breakloop.domain.repository

import com.dreamteam.breakloop.domain.OfflineActivity

interface ActivityRepository {
    fun getCatalog(): List<OfflineActivity>
    fun getActivity(id: String): OfflineActivity?
}