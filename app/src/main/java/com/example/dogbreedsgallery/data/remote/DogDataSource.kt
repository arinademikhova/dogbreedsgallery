
package com.example.dogbreedsgallery.data.remote
import com.example.dogbreedsgallery.data.remote.BreedDto
import com.example.dogbreedsgallery.data.remote.DogImageDto
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
