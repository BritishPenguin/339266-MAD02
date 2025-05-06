package com.example.a339266_md02.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Object to create and manage retrofit instances for coingecko usage
object RetrofitInstance {

    //API interface for CoinGecko
    val api: CoinGeckoApi by lazy {
        Retrofit.Builder()
            // URL for API requirest
            .baseUrl("https://api.coingecko.com/api/v3/")

            // Converts JSON to Cotlin using Gson
            .addConverterFactory(GsonConverterFactory.create())

            //Creates retrofit instance
            .build()

            // Creates CoinGecko Interface
            .create(CoinGeckoApi::class.java)
    }
}