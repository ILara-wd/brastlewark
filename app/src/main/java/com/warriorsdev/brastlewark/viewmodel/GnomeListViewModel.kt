package com.warriorsdev.brastlewark.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.warriorsdev.brastlewark.model.Gnome
import com.warriorsdev.brastlewark.repository.CachedPhotoRepository
import com.warriorsdev.brastlewark.repository.GnomeRepository
import com.warriorsdev.brastlewark.utils.forLiveData
import com.warriorsdev.brastlewark.utils.getRangeFrom
import com.warriorsdev.brastlewark.utils.has
import com.warriorsdev.brastlewark.utils.map
import com.warriorsdev.brastlewark.utils.runOnWorkerThread

/**
 * ViewModel class that returns a List of gnomes.
 * @param gnomeRepository Repository class that fetches the data of gnomes from a List.
 * @param cachedPhotoRepository Repository class that fetches the data of picture from the cache archive.
 */
class GnomeListViewModel(
    private val gnomeRepository: GnomeRepository,
    private val cachedPhotoRepository: CachedPhotoRepository
) : ViewModel() {

    /**
     * Hair colors retrieved for gnomes filtering.
     */
    var hairColors = setOf<String>()

    /**
     * Hair colors retrieved for gnomes filtering.
     */
    var professions = setOf<String>()

    /**
     * Weak references from the first fetch of gnomes.
     */
    private val gnomes = mutableListOf<Gnome>()

    /**
     * MutableLiveData reporting changes of the gnome list such as filters applied.
     */
    private val gnomesMutableLiveData = MutableLiveData<Pair<List<Gnome>?, Exception?>>()
    /**
     * Immutable providing of the gnomes live data.
     */
    val gnomesLiveData = gnomesMutableLiveData as LiveData<Pair<List<Gnome>?, Exception?>>

    /**
     * Retrieves the list of gnomes from the [gnomeRepository] class.
     */
    fun getGnomeList() = runOnWorkerThread {
        if (gnomes.isEmpty()) {
            gnomeRepository.getGnomes { result ->
                result.first?.let { gnomesListResult ->
                    gnomes.addAll(gnomesListResult)
                    if (gnomesListResult.isNotEmpty()) {
                        gnomes.sortBy { gnome -> gnome.name }
                        gnomes.forEach { gnome ->
                            hairColors = hairColors.plus(gnome.hairColor)
                            professions = professions.plus(gnome.professions)
                        }
                    }
                    gnomesMutableLiveData.postValue(Pair(gnomes, null))
                } ?: run {
                    gnomesMutableLiveData.postValue(result)
                }
            }
        } else {
            gnomesMutableLiveData.postValue(Pair(gnomes, null))
        }
    }

    /**
     * Retrieves the age filter settings.
     */
    fun getAgeSettings() = forLiveData {
        gnomes.getRangeFrom({ it.age }, { it.first.age..it.second.age })
    }

    /**
     * Retrieves the height filter settings for the gnomes.
     */
    fun getHeightSettings() = forLiveData {
        gnomes.getRangeFrom({ it.height }, { it.first.height..it.second.height })
    }

    /**
     * Retrieves the height filter settings for the gnomes.
     */
    fun getWeightSettings() = forLiveData {
        gnomes.getRangeFrom({ it.weight }, { it.first.weight..it.second.weight })
    }

    /**
     * Retrieves the friends fitler settings for the app.
     */
    fun getFriendsSettings() = forLiveData {
        gnomes.getRangeFrom({ it.friends.size }, { it.first.friends.size..it.second.friends.size })
    }

    /**
     * Filters gnomes by the given filters values.
     *
     * @param ageRange Range for filtering the gnomes by age
     * @param heightRange Range for filtering the gnomes by height
     * @param weightRange Range for filtering the gnomes by weight
     * @param friendsRange Range for filtering the gnomes by friends amount
     * @param hairColors Range for filtering the gnomes by hair color
     * @param professions Range for filtering the gnomes by professions
     */
    fun filterGnomes(
        ageRange: IntRange,
        heightRange: IntRange,
        weightRange: IntRange,
        friendsRange: IntRange,
        hairColors: Set<String>,
        professions: Set<String>
    ) = gnomesMutableLiveData.map {
        Pair(gnomes.filter {
            val ageInRange = ageRange.has(it.age)
            val weightInRange = weightRange.has(it.weight)
            val heightIntRange = heightRange.has(it.height)
            val friendsInRage = friendsRange.has(it.friends.size)
            val withHairColor =
                if (hairColors.isEmpty()) true else hairColors.contains(it.hairColor)
            val withProfessions =
                if (professions.isEmpty()) true else it.professions.containsAll(professions)
            ageInRange && weightInRange && friendsInRage && heightIntRange && withHairColor && withProfessions
        }.sortedBy { it.name }, null)
    }

    /**
     * Searches for a gnome by its name.
     * @param query Name of the gnome.
     */
    fun searchForGnomeByName(query: String) = gnomesMutableLiveData.map {
        Pair(if (query.isBlank()) {
            gnomes
        } else {
            gnomes.filter { it.name.contains(query, true) }
        }, null)
    }

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
            GnomeListViewModel(repository, photoRepository) as T
    }
}
