package com.warriorsdev.brastlewark.datasource.local

import android.app.Application
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Class that helps access data via SQLite in an easier manner.
 * Providing access to the app database helpers.
 *
 * @param ctx - Context required to create the local database connection.
 */
class BrastlewarkDatabase private constructor(ctx: Context) :
    SQLiteOpenHelper(ctx, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        /**
         * Name for the local database.
         */
        private const val DATABASE_NAME = "brastlewark.db"
        /**
         * Defines the database version.
         */
        private const val DATABASE_VERSION = 1

        /**
         * Reference to the singleton instance of this class.
         */
        private var INSTANCE: BrastlewarkDatabase? = null

        /**
         * Retrieves the singleton instance of this database helper.
         * @param app Application object to retrieve the singleton.
         */
        fun getInstance(app: Application): BrastlewarkDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: createInstance(app).also { INSTANCE = it }
        }

        /**
         * Creates an instance of this database helper class.
         * @param app Application object to retrieve the singleton.
         */
        private fun createInstance(app: Application): BrastlewarkDatabase {
            val database = BrastlewarkDatabase(app)
            val writableDataBase = database.writableDatabase
            return database.apply {
                gnomeDataBaseHelper = GnomeDataBaseHelper(writableDataBase)
                cachedPhotoDataBaseHelper = CachedPhotoDataBaseHelper(writableDataBase)
            }
        }
    }

    /**
     * Reference to the [GnomeDataBaseHelper] class.
     */
    private lateinit var gnomeDataBaseHelper: GnomeDataBaseHelper

    /**
     * Reference to the [CachedPhotoDataBaseHelper] class.
     */
    private lateinit var cachedPhotoDataBaseHelper: CachedPhotoDataBaseHelper

    /**
     * Callback triggered when the database gets created.
     * @param db Created database by the SQLite APIs.
     */
    override fun onCreate(db: SQLiteDatabase?) {
        gnomeDataBaseHelper = GnomeDataBaseHelper(db).also { it.createTable() }
        cachedPhotoDataBaseHelper = CachedPhotoDataBaseHelper(db).also { it.createTable() }
    }

    /**
     * Callback triggered when the database has been upgraded by upgrading the [DATABASE_VERSION] database version.
     * @param db Created database by the SQLite APIs.
     * @param oldVersion Old database version value
     * @param newVersion New database version value
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        GnomeDataBaseHelper(db).nukeTable()
        CachedPhotoDataBaseHelper(db).nukeTable()
        onCreate(db)
    }

    /**
     * Retrieves the reference for the [gnomeDataBaseHelper].
     */
    fun getGnomeDataBaseHelper() = gnomeDataBaseHelper

    /**
     * Retrieves the reference for the [cachedPhotoDataBaseHelper].
     */
    fun getCachedPhotoDataBaseHelper() = cachedPhotoDataBaseHelper
}