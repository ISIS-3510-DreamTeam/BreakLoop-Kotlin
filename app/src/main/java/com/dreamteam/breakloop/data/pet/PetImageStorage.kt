package com.dreamteam.breakloop.data.pet

import android.content.Context
import com.dreamteam.breakloop.domain.PetMood
import java.io.File
import java.io.IOException

class PetImageStorage(context: Context) {

    //private folders
    private val folder = File(context.filesDir, "pet")              // current pet
    private val tempFolder = File(context.filesDir, "pet_download") // new pet being downloaded

    // One file per mood, named "HAPPY" / "SAD".
    fun getImageFile(mood: PetMood): File? {
        val file = File(folder, mood.name)
        return if (file.exists()) file else null
    }

    fun startDownload() {
        tempFolder.deleteRecursively()
        tempFolder.mkdirs()
    }

    fun saveDownloadedImage(moodName: String, bytes: ByteArray) {
        File(tempFolder, moodName).writeBytes(bytes)
    }

    fun finishDownload() {
        folder.deleteRecursively() // get rid of the previous pet's images
        if (!tempFolder.renameTo(folder)) {
            throw IOException("Could not save pet images")
        }
    }
}