package com.example.a339266_md02.network

import retrofit2.http.GET
import retrofit2.http.Query

// interface that communicates with CoinGecko's API using Retrofit
interface CoinGeckoApi {

    // GET request to the coingecko:
    // https://api.coingecko.com/api/v3/simple/price?ids={ids}&vs_currencies={vsCurrencies}
    @GET("simple/price")
    suspend fun getPrice(
        // Comma-separated list of crypto IDs
        @Query("ids") ids: String,

        //Comma-separated list of currencies
        @Query("vs_currencies") vsCurrencies: String
    ): Map<String, Map<String, Double>>
    // Returns a nested map:
    // Example: { "bitcoin": { "gbp": 23450.23 } }
}