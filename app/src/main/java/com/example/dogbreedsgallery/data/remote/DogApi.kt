
package com.example.dogbreedsgallery.data.remote

import com.example.dogbreedsgallery.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.http.GET
import retrofit2.http.Query

import com.example.dogbreedsgallery.data.remote.BreedDto
import com.example.dogbreedsgallery.data.remote.DogImageDto

// Интерфейс с запросами к TheDogAPI
interface DogApiService {

    // Получение списка всех пород
    @GET("breeds")
    suspend fun getBreeds(): List<BreedDto>

    // Получение фотографий конкретной породы
    @GET("images/search")
    suspend fun getBreedImages(
        @Query("breed_ids") breedId: Int,
        @Query("limit") limit: Int = 20
    ): List<DogImageDto>
}


// Настройка и создание подключения к API
object DogApiClient {

    private const val BASE_URL = "https://api.thedogapi.com/v1/"


    // Настройка Moshi для обработки JSON
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // Логирование HTTP-запросов
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
        redactHeader("x-api-key")
    }

    // Настройка OkHttp и передача API-ключа
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
