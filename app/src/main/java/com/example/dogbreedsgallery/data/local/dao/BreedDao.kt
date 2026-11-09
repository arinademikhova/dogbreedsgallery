
package com.example.dogbreedsgallery.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dogbreedsgallery.data.local.entity.BreedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BreedDao {

    // Получение всех пород
    @Query("SELECT * FROM breeds ORDER BY name ASC")
    fun getAllBreeds(): Flow<List<BreedEntity>>

    // Получение только избранных пород
    @Query("SELECT * FROM breeds WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteBreeds(): Flow<List<BreedEntity>>

    // Получение конкретной породы по ID
    @Query("SELECT * FROM breeds WHERE id = :breedId")
    fun getBreedById(breedId: Int): Flow<BreedEntity?>

    // Добавление списка пород в базу
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreeds(breeds: List<BreedEntity>)

    // Изменение статуса избранного
    @Query("UPDATE breeds SET isFavorite = :favorite WHERE id = :breedId")
    suspend fun updateFavorite(breedId: Int, favorite: Boolean)

    // Удаление всех пород
    @Query("DELETE FROM breeds")
    suspend fun deleteAllBreeds()
}
