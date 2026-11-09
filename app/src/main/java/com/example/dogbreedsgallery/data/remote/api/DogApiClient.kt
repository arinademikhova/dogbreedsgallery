
package com.example.dogbreedsgallery.data.remote.api

import com.example.dogbreedsgallery.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object DogApiClient {

    private const val BASE_URL = "https://api.thedogapi.com/v1/"

    // Настройка Moshi
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // Логирование HTTP-запросов
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Передача API-ключа
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->

            val request = chain.request()
                .newBuilder()
                .addHeader("x-api-key", BuildConfig.DOG_API_KEY)
                .build()

            chain.proceed(request)
        }
        .addInterceptor(loggingInterceptor)
        .build()

    // Создание Retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(
            MoshiConverterFactory.create(moshi)
        )
        .build()

    // Создание API-сервиса
    val apiService: DogApiService =
        retrofit.create(DogApiService::class.java)
}
