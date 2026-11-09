package com.example.dogbreedsgallery.data.local.datasource

import com.example.dogbreedsgallery.data.local.dao.BreedDao
import com.example.dogbreedsgallery.data.local.entity.BreedEntity
import kotlinx.coroutines.flow.Flow

class LocalDataSource(
    private val breedDao: BreedDao
) {

    // Получение всех пород
    fun getAllBreeds(): Flow<List<BreedEntity>> {
        return breedDao.getAllBreeds()
    }

    // Получение избранных пород
    fun getFavoriteBreeds(): Flow<List<BreedEntity>> {
        return breedDao.getFavoriteBreeds()
    }

    // Получение породы по ID
    fun getBreedById(breedId: Int): Flow<BreedEntity?> {
        return breedDao.getBreedById(breedId)
    }

    // Сохранение списка пород
    suspend fun saveBreeds(breeds: List<BreedEntity>) {
        breedDao.insertBreeds(breeds)
    }

    // Изменение статуса избранного
    suspend fun updateFavorite(breedId: Int, favorite: Boolean) {
        breedDao.updateFavorite(breedId, favorite)
    }

    // Удаление всех пород
    suspend fun deleteAllBreeds() {
        breedDao.deleteAllBreeds()
    }
}