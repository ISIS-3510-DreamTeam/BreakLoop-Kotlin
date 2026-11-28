package com.dreamteam.breakloop.data.local.entity

import androidx.room.Entity

/** One unlocked achievement of one user. A row only exists once the badge is unlocked. */
@Entity(
    tableName = "user_achievements",
    primaryKeys = ["uid", "achievementId"]
)
data class UserAchievementEntity(
    val uid: String,
    val achievementId: String,
    val unlockedAt: Long,          // epoch millis
    val synced: Boolean = false    // false until the backend has it
)