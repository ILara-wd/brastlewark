package com.warriorsdev.brastlewark.repository

import com.warriorsdev.brastlewark.datasource.local.GnomeDataBaseHelper
import com.warriorsdev.brastlewark.datasource.local.SharedPreferencesDataSource
import com.warriorsdev.brastlewark.datasource.network.GnomeApiService
import com.warriorsdev.brastlewark.model.Gnome
import com.warriorsdev.brastlewark.utils.runOnWorkerThread

/**
 * Class that serves as the single source of truth for all the gnome related data.
 * @param remoteDataSource Data source class that retrieves gnomes data from the internet.
 * @param localDataSource Data source class that retrieves gnome data from the local data base.
 * @param sharedPreferencesDataSource Data source class for retrieving the caches validities.
 */
class GnomeRepository(
    private val remoteDataSource: GnomeApiService,
    private val localDataSource: GnomeDataBaseHelper,
    private val sharedPreferencesDataSource: SharedPreferencesDataSource
) {
    /**
     * Retrieves the gnome data from the internet.
     * @param onComplete Callback reporting the result of retrieving the data from the internet.
     */
    private fun getGnomesByNetwork(onComplete: (result: Pair<List<Gnome>?, Exception?>) -> Unit) =
        remoteDataSource.getJSONResource({
            localDataSource.nukeTable()
            it.forEach(localDataSource::create)
            sharedPreferencesDataSource.generateGnomeCache()
            onComplete(Pair(it, null))
        }, {
            onComplete(Pair(null, it))
        })

    /**
     * Retrieves the gnomes from the local data base.
     * @param onComplete Callback reporting the result of retrieving the gnomes from the data base.
     */
    private fun getGnomesByCache(onComplete: (result: Pair<List<Gnome>?, Exception?>) -> Unit) =
        try {
            onComplete(Pair(localDataSource.getAllGnomes(), null))
        } catch (e: Exception) {
            onComplete(Pair(null, e))
        }

    /**
     * Retrieves the gnomes from the various data source classes.
     * @param onComplete callback reporting the result of retrieving the gnomes data.
     */
    fun getGnomes(onComplete: (result: Pair<List<Gnome>?, Exception?>) -> Unit) =
        runOnWorkerThread {
            if (sharedPreferencesDataSource.isGnomeCacheValid()) {
                getGnomesByCache(onComplete)
            } else {
                getGnomesByNetwork(onComplete)
            }
        }

    /**
     * Retrieves the gnome from the various data sources by its name.
     * @param name The name of the gnome.
     * @param onComplete callback reporting the result of the name query.
     */
    fun getGnomeByName(name: String, onComplete: (result: Pair<Gnome?, Exception?>) -> Unit) =
        runOnWorkerThread {
            try {
                onComplete(Pair(localDataSource.getGnomeByName(name), null))
            } catch (e: Exception) {
                onComplete(Pair(null, e))
            }
        }
}