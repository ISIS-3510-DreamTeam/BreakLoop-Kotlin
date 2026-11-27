package com.dreamteam.breakloop.domain

data class MyProfile(
    val uid: String,
    val name: String,
    val localPhotoPath: String?,
    val remotePhotoUrl: String?,
    val pendingUpload: Boolean,

)
