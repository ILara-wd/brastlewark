package com.warriorsdev.brastlewark.repository

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.warriorsdev.brastlewark.datasource.local.CachedPhotoDataBaseHelper
import com.warriorsdev.brastlewark.datasource.local.SharedPreferencesDataSource
import com.warriorsdev.brastlewark.datasource.network.GnomeApiService
import com.warriorsdev.brastlewark.utils.getFileNameFromURL
import com.warriorsdev.brastlewark.utils.runOnWorkerThread
import java.io.File
import java.io.FileOutputStream

/**
 * Class that serves as the single source of truth for the photo cache archive.
 * @param app Application object to retrieve the local data source.
 * @param remoteDataSource Data source that retrieves the photos from the internet.
 * @param localDataSource Database helper for the photo cache archive.
 * @param sharedPreferencesDataSource Source for the cache archives validity and registers.
 */
class CachedPhotoRepository(
    private val app: Application,
    private val remoteDataSource: GnomeApiService,
    private val localDataSource: CachedPhotoDataBaseHelper,
    private val sharedPreferencesDataSource: SharedPreferencesDataSource
) {
    /**
     * Saves a bitmap in cache. Creating a File in the system cache directory
     * and storing the path of said file and the URL of the photo in the
     * database.
     *
     * @param bitmap The bitmap to be saved in the file.
     * @param src The source of the bitmap.
     */
    private fun storeBitmapInCache(bitmap: Bitmap, src: String) = try {
        val picName = src.getFileNameFromURL()
        val filePath = File(app.cacheDir, "cached-$picName").path
        val outputStream = FileOutputStream(filePath)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        localDataSource.create(src, filePath)
        sharedPreferencesDataSource.generatePictureCache(picName)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    /**
     * Retrieves the picture from the network.
     * @param src The source of the picture from the internet.
     * @param onComplete Callback returning the image as a bitmap.
     */
    private fun getPictureByNetwork(src: String, onComplete: (Pair<Bitmap?, Exception?>) -> Unit) =
        remoteDataSource.getBitmapFromURL(src, {
            storeBitmapInCache(it, src)
            onComplete(Pair(it, null))
        }, {
            onComplete(Pair(null, it))
        })

    /**
     * Retrieves the cached Bitmap file from the cache archive.
     * @param src The source (from the internet) of the picture.
     * @param onComplete Callback that reports the retrieval of the bitmap.
     */
    private fun getPictureByCache(src: String, onComplete: (Pair<Bitmap?, Exception?>) -> Unit) =
        try {
            val path = localDataSource.getCachedPicture(src)
            val bitmap = BitmapFactory.decodeFile(path)
            onComplete(Pair(bitmap, null))
        } catch (e: Exception) {
            onComplete(Pair(null, e))
        }

    /**
     * Retrieves the picture by the given source from all the various data sources.
     * @param onComplete Callback reporting the result.
     */
    fun getPicture(src: String, onComplete: (Pair<Bitmap?, Exception?>) -> Unit) =
        runOnWorkerThread {
            if (sharedPreferencesDataSource.isPictureCacheValid(src.getFileNameFromURL())) {
                getPictureByCache(src, onComplete)
            } else {
                getPictureByNetwork(src, onComplete)
            }
        }
}