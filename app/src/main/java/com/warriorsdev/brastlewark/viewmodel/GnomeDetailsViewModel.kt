package com.warriorsdev.brastlewark.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.warriorsdev.brastlewark.model.Gnome
import com.warriorsdev.brastlewark.repository.CachedPhotoRepository
import com.warriorsdev.brastlewark.repository.GnomeRepository
import com.warriorsdev.brastlewark.utils.runOnWorkerThread

/**
 * ViewModel class that uses repository classes to provide data when the UI requires it.
 * @param gnomeRepository Repository class for the gnome data.
 * @param cachedPhotoRepository Repository class for the photo cache archive.
 */
class GnomeDetailsViewModel(
    private val gnomeRepository: GnomeRepository,
    private val cachedPhotoRepository: CachedPhotoRepository
) : ViewModel() {
    /**
     * Retrieves a gnome using the [gnomeRepository] class.
     * @param name Name of the gnome to search.
     */
    fun getGnome(name: String) = MutableLiveData<Pair<Gnome?, Exception?>>().apply {
        runOnWorkerThread { gnomeRepository.getGnomeByName(name) { postValue(it) } }
    } as LiveData<Pair<Gnome?, Exception?>>

    /**
     * Retrieves the picture from the cache archive.
     * @param src HTTP url source of the picture.
     */
    fun getGnomePicture(src: String) = MutableLiveData<Pair<Bitmap?, Exception?>>().apply {
        runOnWorkerThread { cachedPhotoRepository.getPicture(src) { postValue(it) } }
    } as LiveData<Pair<Bitmap?, Exception?>>

    /**
     * Factory class that describes how to create an instance of this ViewModel class.
     */
    class Factory(
        private val repository: GnomeRepository,
        private val photoRepository: CachedPhotoRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            GnomeDetailsViewModel(repository, photoRepository) as T
    }
}
