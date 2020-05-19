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
class GnomeDataBaseHelperTest {
    @Mock
    lateinit var dataBase: SQLiteDatabase
    @Mock
    lateinit var mockCursor: Cursor
    private lateinit var testSubject: GnomeDataBaseHelper

    @Before
    fun setup() {
        testSubject = GnomeDataBaseHelper(dataBase)
    }

    @Test
    fun getAllGnomes() {
        val testIndex = -1

        Mockito.`when`(dataBase.query("gnomes", null, null, null, null, null, null))
            .thenReturn(mockCursor)
        Mockito.`when`(mockCursor.getColumnIndex(Mockito.anyString())).thenReturn(testIndex)
        Mockito.`when`(mockCursor.getInt(testIndex)).thenReturn(0)
        Mockito.`when`(mockCursor.getString(testIndex)).thenReturn("")
        Mockito.`when`(mockCursor.moveToNext()).thenReturn(true).thenReturn(false)

        val list = testSubject.getAllGnomes()

        assert(list.size == 1)
    }

    @Test
    fun getGnomeByName() {
        val testIndex = -1
        val testName = "Im a gnome!"

        Mockito.`when`(
            dataBase.query(
                "gnomes",
                null,
                "name = ?",
                arrayOf(testName),
                null,
                null,
                null
            )
        )
            .thenReturn(mockCursor)
        Mockito.`when`(mockCursor.getColumnIndex(Mockito.anyString())).thenReturn(testIndex)
        Mockito.`when`(mockCursor.getInt(testIndex)).thenReturn(testIndex)
        Mockito.`when`(mockCursor.getString(testIndex)).thenReturn(testName)
        Mockito.`when`(mockCursor.moveToNext()).thenReturn(true)

        val gnome = testSubject.getGnomeByName(testName)

        assertEquals(testName, gnome.name)
    }
}