package com.warriorsdev.brastlewark.datasource.local

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.warriorsdev.brastlewark.datasource.local.GnomeDataBaseHelper.GnomeEntry.AGE_COLUMN
import com.warriorsdev.brastlewark.datasource.local.GnomeDataBaseHelper.GnomeEntry.FRIENDS_COLUMN
import com.warriorsdev.brastlewark.datasource.local.GnomeDataBaseHelper.GnomeEntry.HAIR_COLOR_COLUMN
import com.warriorsdev.brastlewark.datasource.local.GnomeDataBaseHelper.GnomeEntry.HEIGHT_COLUMN
import com.warriorsdev.brastlewark.datasource.local.GnomeDataBaseHelper.GnomeEntry.NAME_COLUMN
import com.warriorsdev.brastlewark.datasource.local.GnomeDataBaseHelper.GnomeEntry.PROFESSIONS_COLUMN
import com.warriorsdev.brastlewark.datasource.local.GnomeDataBaseHelper.GnomeEntry.SQL_CREATE_TABLE
import com.warriorsdev.brastlewark.datasource.local.GnomeDataBaseHelper.GnomeEntry.TABLE_NAME
import com.warriorsdev.brastlewark.datasource.local.GnomeDataBaseHelper.GnomeEntry.THUMBNAIL_URL_COLUMN
import com.warriorsdev.brastlewark.datasource.local.GnomeDataBaseHelper.GnomeEntry.WEIGHT_COLUMN
import com.warriorsdev.brastlewark.model.Gnome
import com.warriorsdev.brastlewark.model.ternary
import com.warriorsdev.brastlewark.utils.asList
import com.warriorsdev.brastlewark.utils.asString

/**
 * Helper class that allows access to all the gnome rows in the local database.
 */
class GnomeDataBaseHelper(private val db: SQLiteDatabase?) {
    private object GnomeEntry : BaseColumns {
        const val TABLE_NAME = "gnomes"
        const val NAME_COLUMN = "name"
        const val THUMBNAIL_URL_COLUMN = "thumbnail_url"
        const val AGE_COLUMN = "age"
        const val WEIGHT_COLUMN = "weight"
        const val HEIGHT_COLUMN = "height"
        const val HAIR_COLOR_COLUMN = "hair_color"
        const val PROFESSIONS_COLUMN = "professions"
        const val FRIENDS_COLUMN = "friends"

        const val SQL_CREATE_TABLE = "CREATE TABLE $TABLE_NAME (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                "$NAME_COLUMN TEXT, " +
                "$THUMBNAIL_URL_COLUMN TEXT, " +
                "$AGE_COLUMN INTEGER, " +
                "$WEIGHT_COLUMN INTEGER, " +
                "$HEIGHT_COLUMN INTEGER, " +
                "$HAIR_COLOR_COLUMN TEXT, " +
                "$PROFESSIONS_COLUMN TEXT, " +
                "$FRIENDS_COLUMN TEXT)"
    }

    /**
     * Creates the table for this model.
     */
    fun createTable() {
        db?.execSQL(SQL_CREATE_TABLE)
    }

    /**
     * Deletes all contents in the table.
     */
    fun nukeTable() {
        db?.delete(TABLE_NAME, null, null)
    }

    /**
     * Adds a gnome register in the table.
     */
    fun create(gnome: Gnome) {
        val values = ContentValues().apply {
            gnome.run {
                put(BaseColumns._ID, id)
                put(NAME_COLUMN, name)
                put(THUMBNAIL_URL_COLUMN, thumbnailUrl)
                put(AGE_COLUMN, age)
                put(WEIGHT_COLUMN, weight)
                put(HEIGHT_COLUMN, height)
                put(HAIR_COLOR_COLUMN, hairColor)
                put(PROFESSIONS_COLUMN, professions.asString())
                put(FRIENDS_COLUMN, friends.asString())
            }
        }

        db?.insert(TABLE_NAME, null, values)?.toInt()
    }

    /**
     * Retrieves a gnome from a database cursor.
     * @param cursor Database cursor containing the Gnome data.
     * @param closeCursor If the cursor has to be closed after retrieving the data.
     */
    private fun getGnomeFromCursor(cursor: Cursor?, closeCursor: Boolean = false) = cursor?.run {
        val id = getInt(getColumnIndex(BaseColumns._ID))
        val name = getString(getColumnIndex(NAME_COLUMN))
        val thumbnail = getString(getColumnIndex(THUMBNAIL_URL_COLUMN))
        val age = getInt(getColumnIndex(AGE_COLUMN))
        val weight = getInt(getColumnIndex(WEIGHT_COLUMN))
        val height = getInt(getColumnIndex(HEIGHT_COLUMN))
        val hairColor = getString(getColumnIndex(HAIR_COLOR_COLUMN))
        val professions = getString(getColumnIndex(PROFESSIONS_COLUMN)).asList()
        val friends = getString(getColumnIndex(FRIENDS_COLUMN)).asList()
        val genre = (hairColor == "Pink").ternary("♀ FEMALE")?: "♂ MALE"

        if (closeCursor) close()
        Gnome(id, name, thumbnail, age, weight, height, hairColor, genre, professions, friends)
    } ?: Gnome()

    /**
     * Retrieves all the gnomes records.
     */
    fun getAllGnomes(): List<Gnome> {
        val cursor = db?.query(TABLE_NAME, null, null, null, null, null, null)
        return mutableListOf<Gnome>().run {
            while (cursor?.moveToNext() == true) {
                add(getGnomeFromCursor(cursor))
            }
            cursor?.close()
            filter { it.id > -1 }
        }
    }

    /**
     * Retrieves a gnome record by its name.
     * @param gnomeName Name of the gnome.
     */
    fun getGnomeByName(gnomeName: String): Gnome {
        val selection = "$NAME_COLUMN = ?"
        val args = arrayOf(gnomeName)
        val cursor = db?.query(TABLE_NAME, null, selection, args, null, null, null)
        return if (cursor?.moveToNext() == true) {
            getGnomeFromCursor(cursor, true)
        } else {
            Gnome()
        }
    }
}