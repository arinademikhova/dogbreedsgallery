
package com.example.dogbreedsgallery.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

// 1. Entity — описание таблицы пород собак

@Entity(tableName = "breeds")
data class BreedEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val temperament: String,
    val origin: String?,
    val lifeSpan: String,
    val description: String?,
    val imageUrl: String,
    val isFavorite: Boolean = false
)

// 2. DAO — запросы к базе данных

@Dao
interface BreedDao {

    @Query("SELECT * FROM breeds ORDER BY name ASC")
    fun getAllBreeds(): Flow<List<BreedEntity>>

    @Query("SELECT * FROM breeds WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteBreeds(): Flow<List<BreedEntity>>

    @Query("SELECT * FROM breeds WHERE id = :breedId")
    fun getBreedById(breedId: Int): Flow<BreedEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreeds(breeds: List<BreedEntity>)

    @Query("UPDATE breeds SET isFavorite = :favorite WHERE id = :breedId")
    suspend fun updateFavorite(breedId: Int, favorite: Boolean)

    @Query("DELETE FROM breeds")
    suspend fun deleteAllBreeds()
}

// 3. Database — создание базы данных Room

@Database(
    entities = [BreedEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun breedDao(): BreedDao
}

// 4. LocalDataSource — работа с локальной базой

class LocalDataSource(
    private val breedDao: BreedDao
) {

    fun getAllBreeds(): Flow<List<BreedEntity>> {
        return breedDao.getAllBreeds()
    }

    fun getFavoriteBreeds(): Flow<List<BreedEntity>> {
        return breedDao.getFavoriteBreeds()
    }

    fun getBreedById(breedId: Int): Flow<BreedEntity?> {
        return breedDao.getBreedById(breedId)
    }

    suspend fun saveBreeds(breeds: List<BreedEntity>) {
        breedDao.insertBreeds(breeds)
    }

    suspend fun updateFavorite(breedId: Int, favorite: Boolean) {
        breedDao.updateFavorite(breedId, favorite)
    }

    suspend fun deleteAllBreeds() {
        breedDao.deleteAllBreeds()
    }
}
