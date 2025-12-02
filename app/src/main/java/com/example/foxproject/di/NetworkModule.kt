package com.example.foxproject.di

import com.example.foxproject.data.api.FoxApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.Gson

object NetworkModule {
    private const val BASE_URL = "https://randomfox.ca/"

    private val gson = Gson()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val foxApiService: FoxApiService = retrofit.create(FoxApiService::class.java)
}