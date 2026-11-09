
package com.example.dogbreedsgallery.data.remote

class DogDataSource(
    private val dogApiService: DogApiService
) {

    // Получение списка пород из интернета
    suspend fun getBreeds(): List<BreedDto> {
        return dogApiService.getBreeds()
    }

    // Получение фотографий конкретной породы
    suspend fun getBreedImages(breedId: Int): List<DogImageDto> {
        return dogApiService.getBreedImages(breedId)
    }
}
