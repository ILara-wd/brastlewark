package com.warriorsdev.brastlewark.utils.di

import android.app.Application
import com.warriorsdev.brastlewark.datasource.local.BrastlewarkDatabase
import com.warriorsdev.brastlewark.datasource.local.SharedPreferencesDataSource
import com.warriorsdev.brastlewark.datasource.network.GnomeApiService
import com.warriorsdev.brastlewark.repository.CachedPhotoRepository
import com.warriorsdev.brastlewark.repository.GnomeRepository
import com.warriorsdev.brastlewark.viewmodel.GnomeDetailsViewModel
import com.warriorsdev.brastlewark.viewmodel.GnomeListViewModel

/**
 * Data class that creates all the necessary classes required by other classes
 * and provides **dependency inversion** when said classes request them.
 *
 * @param app Application class that will creates this class singleton.
 */
class Injector private constructor(app: Application) {
    companion object {
        /**
         * Singleton instance for this class.
         */
        private var INSTANCE: Injector? = null

        /**
         * Retrieves the singleton instance for this class.
         */
        fun getInstance(app: Application) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: createInstance(app)
        }

        /**
         * Creates the instance for this class.
         */
        private fun createInstance(app: Application) = Injector(app)
    }

    /**
     * Lazy initialization of the [GnomeApiService] class.
     */
    private val gnomeApiService by lazy { GnomeApiService }

    /**
     * Lazy initialization of the [BrastlewarkDatabase]
     */
    private val database by lazy { BrastlewarkDatabase.getInstance(app) }

    /**
     * Lazy initialization of the GnomeDataBaseHelper class.
     */
    private val gnomeDataBaseHelper by lazy { database.getGnomeDataBaseHelper() }

    /**
     * Lazy initialization of the SharedPreferencesDataSource class.
     */
    private val sharedPreferencesDataSource by lazy { SharedPreferencesDataSource(app) }

    /**
     * Lazy initialization of the CachedPhotoDataBaseHelper
     */
    private val cachedPhotoDataBaseHelper by lazy { database.getCachedPhotoDataBaseHelper() }

    /**
     * Lazy initialization of the [GnomeRepository] class.
     */
    private val gnomeRepository by lazy {
        GnomeRepository(gnomeApiService, gnomeDataBaseHelper, sharedPreferencesDataSource)
    }

    /**
     * Lazy initialization of the [CachedPhotoRepository] class.
     */
    private val cachedPhotoRepository by lazy {
        CachedPhotoRepository(
            app,
            gnomeApiService,
            cachedPhotoDataBaseHelper,
            sharedPreferencesDataSource
        )
    }

    /**
     * Provides the [GnomeListViewModel.Factory] class if any class requests it via **dependency inversion**.
     */
    fun provideGnomeListViewModelFactory() =
        GnomeListViewModel.Factory(gnomeRepository, cachedPhotoRepository)

    /**
     * Provides the [GnomeDetailsViewModel.Factory] class if any class requests it via **dependency inversion**.
     */
    fun provideGnomeDetailsViewModelFactory() =
        GnomeDetailsViewModel.Factory(gnomeRepository, cachedPhotoRepository)
}