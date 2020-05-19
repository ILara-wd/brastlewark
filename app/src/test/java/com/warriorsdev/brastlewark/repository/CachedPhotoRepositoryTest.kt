package com.warriorsdev.brastlewark.repository

import com.warriorsdev.brastlewark.datasource.local.CachedPhotoDataBaseHelper
import com.warriorsdev.brastlewark.datasource.local.SharedPreferencesDataSource
import com.warriorsdev.brastlewark.datasource.network.GnomeApiService
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class CachedPhotoRepositoryTest {
    @Mock
    lateinit var remoteDS: GnomeApiService
    @Mock
    lateinit var localDS: CachedPhotoDataBaseHelper
    @Mock
    lateinit var sharedPreferencesDS: SharedPreferencesDataSource

    private lateinit var testSubject: CachedPhotoRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        testSubject = CachedPhotoRepository(
            RuntimeEnvironment.application,
            remoteDS,
            localDS,
            sharedPreferencesDS
        )
    }

    @Test
    fun getPicture_fromNetwork() {
        val testPicName = "myPhoto.jpg"
        Mockito.`when`(sharedPreferencesDS.isPictureCacheValid(testPicName)).thenReturn(false)
        testSubject.getPicture(testPicName) {}
    }

    @Test
    fun getPicture_fromLocal() {
        val testPicName = "myPhoto.jpg"
        Mockito.`when`(sharedPreferencesDS.isPictureCacheValid(testPicName)).thenReturn(true)
        Mockito.`when`(localDS.getCachedPicture(testPicName)).thenReturn("cache-$testPicName")
        testSubject.getPicture(testPicName) {}
    }
}