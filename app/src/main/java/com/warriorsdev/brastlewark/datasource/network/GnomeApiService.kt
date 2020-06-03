package com.warriorsdev.brastlewark.datasource.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.warriorsdev.brastlewark.BuildConfig
import com.warriorsdev.brastlewark.model.Gnome
import com.warriorsdev.brastlewark.utils.asSafeURL
import com.warriorsdev.brastlewark.utils.resize
import com.warriorsdev.brastlewark.utils.runOnWorkerThread
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

/**
 * Kotlin object that handles all the URL connections through the project.
 */
object GnomeApiService {

    private val TIMEOUT = TimeUnit.SECONDS.toMillis(60000L).toInt()

    /**
     * Retrieves a JSON resource from the server URL.
     * @param onSuccess Success callback containing the result.
     * @param onError Callback reporting an error.
     */
    fun getJSONResource(
        onSuccess: (gnomes: List<Gnome>) -> Unit, onError: (e: Exception) -> Unit
    ) = try {
        runOnWorkerThread {
            val url = URL(BuildConfig.SERVER_URL)
            val httpURLConnection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = TIMEOUT
                readTimeout = TIMEOUT
            }

            val jsonString = getResourceAsJsonString(httpURLConnection)
            val population = getPopulationFromJson(jsonString)
            val gnomes = getGnomes(population)

            onSuccess(gnomes)

            httpURLConnection.disconnect()
        }
    } catch (e: Exception) {
        onError(e)
    }

    /**
     * Retrieves the remote resource JSON as a String.
     * @param httpURLConnection
     */
    private fun getResourceAsJsonString(httpURLConnection: HttpURLConnection) =
        BufferedReader(InputStreamReader(httpURLConnection.inputStream)).run {
            StringBuilder().apply { forEachLine { append(it) } }.toString().also { close() }
        }

    /**
     * Retrieves the population of Gnomes from the population JSON string.
     * @param json String containing the JSON data for the population.
     */
    private fun getPopulationFromJson(json: String) = JSONObject(json).getJSONArray("Brastlewark")

    /**
     * Retrieves the list of Gnome population form the JSONArray.
     * @param jsonArray JSONArray to fetch the Gnome population.
     */
    private fun getGnomes(jsonArray: JSONArray) = jsonArray.run {
        mutableListOf<Gnome>().apply {
            for (i in 0 until length()) {
                add(getGnome(getJSONObject(i)))
            }
        } as List<Gnome>
    }

    /**
     * Retrieves a Gnome from a JSONObject.
     * @param jsonObject JSONObject to retrieve the Gnome from.
     */
    private fun getGnome(jsonObject: JSONObject) = jsonObject.run {
        Gnome(
            id = getInt("id"),
            name = getString("name"),
            thumbnailUrl = getString("thumbnail"),
            age = getInt("age"),
            weight = getDouble("weight").toInt(),
            height = getDouble("height").toInt(),
            hairColor = getString("hair_color"),
            professions = getJSONArray("professions").asStringList(),
            friends = getJSONArray("friends").asStringList()
        )
    }

    /**
     * Parses a JSONArray as a List of Strings.
     */
    private fun JSONArray.asStringList() = this.let { jsonArray ->
        mutableListOf<String>().apply {
            for (i in 0 until jsonArray.length()) {
                add(jsonArray.get(i).toString())
            }
        } as List<String>
    }

    /**
     * Retrieves a Bitmap from an URL.
     * @param source URL source for the bitmap.
     * @param onSuccess Function reporting the bitmap from the URL.
     * @param onError Function reporting any exception thrown by this function.
     */
    fun getBitmapFromURL(
        source: String,
        onSuccess: (bitmap: Bitmap) -> Unit,
        onError: (e: Exception) -> Unit
    ) = runOnWorkerThread {
        try {
            val url = URL(source.asSafeURL())
            val httpURLConnection = (url.openConnection() as HttpURLConnection).apply {
                doInput = true
                readTimeout = TIMEOUT
                connectTimeout = TIMEOUT
                connect()
            }

            val responseCode = httpURLConnection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val input = httpURLConnection.inputStream
                val bitmap = BitmapFactory.decodeStream(input)
                onSuccess(bitmap.resize(100, 100))
            } else {
                onError(Exception(responseCode.toString()))
            }

            httpURLConnection.disconnect()
        } catch (e: Exception) {
            onError(e)
        }
    }
}