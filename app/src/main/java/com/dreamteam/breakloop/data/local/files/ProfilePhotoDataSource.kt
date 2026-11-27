package com.dreamteam.breakloop.data.local.files

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max
import androidx.core.graphics.scale


class ProfilePhotoDataSource (
    private val context: Context
){
    private val folder = File(context.filesDir, "profile")
    private fun decodeScaled(uri: Uri): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds =  true
        context.contentResolver.openInputStream(uri)?.use {
            inputStream ->
            BitmapFactory.decodeStream(inputStream, null, options)
        }
        options.inSampleSize = calculateSample(options.outWidth, options.outHeight, 1024)
        options.inJustDecodeBounds = false
        val bitmap = context.contentResolver.openInputStream(uri)?.use {
            inputStream ->
            BitmapFactory.decodeStream(inputStream, null, options)
        } ?: return null

        if ( max(options.outWidth, options.outHeight)>512) {
            val scale = 512f / max(options.outWidth, options.outHeight)
            return bitmap.scale(
                (options.outWidth * scale).toInt(),
                (options.outHeight * scale).toInt()
            )
        }
        else
            return bitmap
    }


    suspend fun save(uri: Uri): String? = withContext(Dispatchers.IO) {
        folder.mkdirs()
        val stamp = System.currentTimeMillis()
        val tmp = File(folder, "avatar_$stamp.tmp")
        try {
            val bitmap = decodeScaled(uri) ?: return@withContext null
            val fileOutputStream = FileOutputStream(tmp)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream)
            fileOutputStream.close()

            val finalFile = File(folder, "avatar_$stamp.jpg")
            tmp.renameTo(finalFile)
            deleteOthers(finalFile)
            finalFile.absolutePath
        } catch (e: Exception){
            tmp.delete()
            null
        }
    }

    private fun calculateSample(wide: Int, tall: Int, target: Int ): Int {
        var sample = 1
        while (max(wide, tall) /(sample*2)>=target) {
            sample *= 2
        }
        return sample
    }

    private fun deleteOthers( except: File){
        folder.listFiles()?.forEach { each ->
            if (each!= except) {
                each.delete()
            }
        }
    }

    fun deleteAll() {
        folder.deleteRecursively()
    }
}