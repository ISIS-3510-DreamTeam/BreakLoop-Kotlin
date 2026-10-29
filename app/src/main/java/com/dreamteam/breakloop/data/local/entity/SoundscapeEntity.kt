
package com.dreamteam.breakloop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "soundscapes")
data class SoundscapeEntity(
    @PrimaryKey
    val name: String,

    val localPath: String,

    val downloadedAt: Long
)
