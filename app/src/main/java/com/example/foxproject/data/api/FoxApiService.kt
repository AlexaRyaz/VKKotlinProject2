package com.example.foxproject.data.api

import com.example.foxproject.data.models.FoxResponse
import retrofit2.http.GET

interface FoxApiService {
    @GET("floof")
    suspend fun getRandomFox(): FoxResponse

    companion object {
        const val BASE_URL = "https://randomfox.ca/"
    }
}