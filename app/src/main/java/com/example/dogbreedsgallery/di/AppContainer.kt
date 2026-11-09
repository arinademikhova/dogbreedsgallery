
package com.example.dogbreedsgallery.di

import android.content.Context
import androidx.room.Room
import com.example.dogbreedsgallery.data.local.AppDatabase
import com.example.dogbreedsgallery.data.local.LocalDataSource
import com.example.dogbreedsgallery.data.remote.DogApiClient
import com.example.dogbreedsgallery.data.remote.DogDataSource
import com.example.dogbreedsgallery.data.repository.BreedRepository

class AppContainer(context: Context) {

    // Создаём локальную базу данных
    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "dog_breeds_database"
    ).build()

    // Создаём источник локальных данных
    private val localDataSource = LocalDataSource(
        database.breedDao()
    )

    // Создаём источник данных из интернета
    private val dogDataSource = DogDataSource(
        DogApiClient.apiService
    )

    // Объединяем источники в Repository
    val breedRepository = BreedRepository(
        localDataSource,
        dogDataSource
    )
}
