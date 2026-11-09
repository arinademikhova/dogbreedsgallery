package com.example.dogbreedsgallery.data.remote.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.OkHttpClient

object DogApiClient {

    private const val BASE_URL = "https://api.thedogapi.com/v1/"

    // Настройка Moshi
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // Настройка OkHttp
    private val okHttpClient = OkHttpClient.Builder()
        .build()

    // Создание Retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    // Создание API-сервиса
    val apiService: DogApiService =
        retrofit.create(DogApiService::class.java)
}