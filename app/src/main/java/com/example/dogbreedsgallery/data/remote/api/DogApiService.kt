package com.example.dogbreedsgallery.data.remote.api

import com.example.dogbreedsgallery.data.remote.dto.BreedDto
import retrofit2.http.GET

interface DogApiService {

    // Получение списка всех пород собак
    @GET("breeds")
    suspend fun getBreeds(): List<BreedDto>
}