package com.example.dogbreedsgallery.data.repository

import com.example.dogbreedsgallery.data.local.datasource.LocalDataSource
import com.example.dogbreedsgallery.data.local.entity.BreedEntity
import com.example.dogbreedsgallery.data.remote.datasource.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class BreedRepository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) {

    // Получение пород из локальной базы
    fun getBreeds(): Flow<List<BreedEntity>> {
        return localDataSource.getAllBreeds()
    }

    // Обновление данных из интернета
    suspend fun refreshBreeds() {

        // Получаем данные с сервера
        val remoteBreeds = remoteDataSource.getBreeds()

        // Получаем текущие данные из локальной базы
        val localBreeds = localDataSource.getAllBreeds().first()

        // Запоминаем избранные породы
        val favoriteIds = localBreeds
            .filter { it.isFavorite }
            .map { it.id }
            .toSet()

        // Преобразуем DTO в Entity
        val breeds = remoteBreeds.map { dto ->

            BreedEntity(
                id = dto.id,
                name = dto.name,
                temperament = dto.temperament ?: "",
                origin = dto.origin,
                lifeSpan = dto.lifeSpan ?: "Не указано",
                description = dto.description,
                imageUrl = dto.image?.url ?: "",
                isFavorite = dto.id in favoriteIds
            )
        }

        // Сохраняем данные в Room
        localDataSource.saveBreeds(breeds)
    }

    // Получение избранных пород
    fun getFavoriteBreeds(): Flow<List<BreedEntity>> {
        return localDataSource.getFavoriteBreeds()
    }

    // Изменение статуса избранного
    suspend fun updateFavorite(breedId: Int, favorite: Boolean) {
        localDataSource.updateFavorite(breedId, favorite)
    }

    // Получение породы по ID
    fun getBreedById(breedId: Int): Flow<BreedEntity?> {
        return localDataSource.getBreedById(breedId)
    }
}