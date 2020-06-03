package com.warriorsdev.brastlewark.datasource.local

import android.app.Application
import android.content.Context
import java.util.Calendar
import java.util.Locale

/**
 * Class that serves as a wrapper for accessing the app SharedPreferences. An XML file that
 * contains the timestamps for various app caches.
 */
class SharedPreferencesDataSource(private val application: Application) {
    private companion object {
        const val GNOME_CACHE_KEY = "gnome_cahe"
        const val PICTURES_CACHE = "pictures_cache"
        const val SHARED_FILE_NAME = "cache"
        const val CACHE_DURATION = 15 // Cache for 15 minutes.
    }

    /**
     * Singleton reference to this app shared preferences.
     */
    private val preferences by lazy {
        application.getSharedPreferences(SHARED_FILE_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Generates a timestamp referencing the time were the stored cache must expire.
     */
    private fun generateCacheTimeStamp() = Calendar.getInstance(Locale.getDefault()).run {
        add(Calendar.MINUTE, CACHE_DURATION)
        timeInMillis
    }

    /**
     * Retrieves the current system time in milliseconds from the epoch.
     */
    private fun getCurrentTimeInMillis() = Calendar.getInstance(Locale.getDefault()).timeInMillis

    /**
     * Stores a timestamp cache in the shared preferences XML file.
     * @param key Cache key for the XML file.
     */
    private fun storeCacheTimeStamp(key: String) = preferences.edit()?.apply {
        putLong(key, generateCacheTimeStamp())
    }?.apply()

    /**
     * Checks if the cache timestamp for the given value is valid against the current system time.
     * @param key Key used to retrieve the cache timestamp.
     */
    private fun isCacheValid(key: String): Boolean {
        val currentTimeInMillis = getCurrentTimeInMillis()
        val cacheTimeInMillis = preferences.getLong(key, currentTimeInMillis)
        return currentTimeInMillis < cacheTimeInMillis
    }

    /**
     * Checks if the gnome cache is valid.
     * @return true if the cache is still valid.
     */
    fun isGnomeCacheValid() = isCacheValid(GNOME_CACHE_KEY)

    /**
     * Generates the gnome cache timestamp.
     */
    fun generateGnomeCache() = storeCacheTimeStamp(GNOME_CACHE_KEY)

    /**
     * Checks if the a picture in the cache archive is valid.
     * @return true if the cache is still valid.
     */
    fun isPictureCacheValid(picName: String) = isCacheValid("${PICTURES_CACHE}_$picName")

    /**
     * Generates the picture cache timestamp.
     */
    fun generatePictureCache(picName: String) = storeCacheTimeStamp("${PICTURES_CACHE}_$picName")
}