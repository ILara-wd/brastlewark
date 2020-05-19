package com.warriorsdev.brastlewark.datasource.local

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CachedPhotoDataBaseHelperTest {
    @Mock
    lateinit var dataBase: SQLiteDatabase
    @Mock
    lateinit var mockCursor: Cursor
    private lateinit var testSubject: CachedPhotoDataBaseHelper

    @Before
    fun setup() {
        testSubject = CachedPhotoDataBaseHelper(dataBase)
    }

    @Test
    fun getCachedPicture() {
        val testSrc = "http://thelastsurvivorfps.com/assets/images/background.jpg"
        Mockito.`when`(
            dataBase.query(
                "cached_pictures",
                arrayOf("path"),
                "url = ?",
                arrayOf(testSrc),
                null,
                null,
                null
            )
        ).thenReturn(mockCursor)

        Mockito.`when`(mockCursor.moveToNext()).thenReturn(true)
        Mockito.`when`(mockCursor.getColumnIndex("path")).thenReturn(1)
        Mockito.`when`(mockCursor.getString(1)).thenReturn(testSrc)

        assertEquals(testSrc, testSubject.getCachedPicture(testSrc))
    }
}