package com.warriorsdev.brastlewark.datasource.local

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.warriorsdev.brastlewark.datasource.local.CachedPhotoDataBaseHelper.CachedPhotoEntry.PATH_COLUMN
import com.warriorsdev.brastlewark.datasource.local.CachedPhotoDataBaseHelper.CachedPhotoEntry.SQL_CREATE_TABLE
import com.warriorsdev.brastlewark.datasource.local.CachedPhotoDataBaseHelper.CachedPhotoEntry.TABLE_NAME
import com.warriorsdev.brastlewark.datasource.local.CachedPhotoDataBaseHelper.CachedPhotoEntry.URL_COLUMN

/**
 * Database helper that allows access to the photos cache archives.
 * @param db Reference to a writable database.
 */
class CachedPhotoDataBaseHelper(private val db: SQLiteDatabase?) {
    private object CachedPhotoEntry : BaseColumns {
        const val TABLE_NAME = "cached_pictures"
        const val URL_COLUMN = "url"
        const val PATH_COLUMN = "path"

        const val SQL_CREATE_TABLE = "CREATE TABLE $TABLE_NAME (" +
                "$URL_COLUMN TEXT PRIMARY KEY, " +
                "$PATH_COLUMN TEXT)"
    }

    /**
     * Creates this table in the database.
     */
    fun createTable() {
        db?.execSQL(SQL_CREATE_TABLE)
    }

    /**
     * Deletes all of this table contents
     */
    fun nukeTable() {
        db?.delete(TABLE_NAME, null, null)
    }

    /**
     * Creates a row in this database table.
     */
    fun create(src: String, path: String): String {
        val values = ContentValues().apply {
            put(URL_COLUMN, src)
            put(PATH_COLUMN, path)
        }
        return db?.insert(TABLE_NAME, null, values).toString()
    }

    /**
     * Retrieves a cached picture by its URL value (id in the table).
     */
    fun getCachedPicture(src: String): String {
        val selection = "$URL_COLUMN = ?"
        val selectionArgs = arrayOf(src)
        return db?.query(
            TABLE_NAME,
            arrayOf(PATH_COLUMN),
            selection,
            selectionArgs,
            null,
            null,
            null
        )?.run {
            if (moveToNext()) {
                getString(getColumnIndex(PATH_COLUMN))
            } else {
                ""
            }
        } ?: ""
    }
}