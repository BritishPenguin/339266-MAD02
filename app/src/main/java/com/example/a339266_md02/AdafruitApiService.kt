package com.example.a339266_md02.network

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

// Object to manage Adafruit API
object AdafruitApiService {

    // Adafruit IO API configurations
    private const val BASE_URL = "https://io.adafruit.com/api/v2"
    private const val USERNAME = "AlexDirichleau"
    private const val FEED_KEY = "purchase-data"
    private const val API_KEY = "aio_MhAG62zG1HFuof1t69vKYCGt2mah"

    // HTTP client for sending and receiving requests
    private val client = OkHttpClient()

    // Sends JSON data to Adafruit
    fun sendData(data: JSONObject, callback: (Boolean, String?) -> Unit) {
        val url = "$BASE_URL/$USERNAME/feeds/$FEED_KEY/data"

        val mediaType = "application/json".toMediaType()
        val body = data.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .addHeader("X-AIO-Key", API_KEY) // Header Authenticator
            .post(body) //Send JSON request
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, e.message) // Failure Notification for Debugging
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(true, null) // Success Notification for Debugging
                } else {
                    callback(false, response.message) //Adafruit Side Error for Debugging
                }
            }
        })
    }

    // Fetches all cryptocurrency purchases from adafruit
    fun fetchData(callback: (List<JSONObject>?, String?) -> Unit) {
        val url = "$BASE_URL/$USERNAME/feeds/$FEED_KEY/data"

        val request = Request.Builder()
            .url(url)
            .addHeader("X-AIO-Key", API_KEY) //Header Authenticator
            .get() //Get Request
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, e.message) //Network or Parsing Error for debugging
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val jsonArray = JSONArray(response.body?.string() ?: "[]")
                    val dataList = mutableListOf<JSONObject>()

                    for (i in 0 until jsonArray.length()) {
                        val dataItem = jsonArray.getJSONObject(i)
                        val valueStr = dataItem.optString("value")

                        try {
                            //Attempt to parse value field into JSON
                            val parsed = JSONObject(valueStr)
                            dataList.add(parsed)
                        } catch (e: Exception) {
                            //Skip if value cant be converted to JSON, for debugging
                        }
                    }

                    callback(dataList, null) //Returns all valid entries
                } else {
                    callback(null, response.message) //API sends error, for debugging
                }
            }
        })
    }
}