
package com.dreamteam.breakloop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey
    val id: String,

    val uid: String,

    val startTime: Long,

    val duration: Int,

    val type: String,

    val status: String,

    val xpEarned: Int,

    val synced: Boolean = false,

    val elapsedSeconds: Int = 0,

    val remainingSeconds: Int = 0,

    val isPaused: Boolean = false,

    val lastUpdatedAt: Long = System.currentTimeMillis(),

    val recoveryCount: Int = 0,

    val lastRecoveredAt: Long? = null
)
