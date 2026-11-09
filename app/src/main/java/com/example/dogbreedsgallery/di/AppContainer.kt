
package com.example.dogbreedsgallery.di

import android.content.Context
import androidx.room.Room

import com.example.dogbreedsgallery.data.local.AppDatabase
import com.example.dogbreedsgallery.data.local.datasource.LocalDataSource
import com.example.dogbreedsgallery.data.remote.api.DogApiClient
import com.example.dogbreedsgallery.data.remote.datasource.RemoteDataSource
import com.example.dogbreedsgallery.data.repository.BreedRepository


class AppContainer(context: Context) {

    // Создаём локальную базу данных Room
    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "dog_breeds_database"
    ).build()


    // Создаём локальный источник данных
    private val localDataSource = LocalDataSource(
        database.breedDao()
    )


    // Создаём удалённый источник данных
    private val remoteDataSource = RemoteDataSource(
        DogApiClient.apiService
    )


    // Создаём Repository
    val breedRepository = BreedRepository(
        localDataSource,
        remoteDataSource
    )
}
