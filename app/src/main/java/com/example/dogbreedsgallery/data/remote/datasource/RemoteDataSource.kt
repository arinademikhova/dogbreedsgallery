package com.example.dogbreedsgallery.data.remote.datasource

import com.example.dogbreedsgallery.data.remote.api.DogApiService
import com.example.dogbreedsgallery.data.remote.dto.BreedDto

class RemoteDataSource(
    private val dogApiService: DogApiService
) {

    // Получение списка пород из интернета
    suspend fun getBreeds(): List<BreedDto> {
        return dogApiService.getBreeds()
    }
}