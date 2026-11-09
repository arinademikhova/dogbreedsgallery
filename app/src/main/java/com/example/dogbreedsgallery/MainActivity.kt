
package com.example.dogbreedsgallery

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue

import com.example.dogbreedsgallery.di.AppContainer
import com.example.dogbreedsgallery.ui.HomeScreen
import com.example.dogbreedsgallery.ui.theme.DogbreedsgalleryTheme
import com.example.dogbreedsgallery.ui.viewmodel.BreedViewModel
import com.example.dogbreedsgallery.ui.viewmodel.BreedViewModelFactory


class MainActivity : ComponentActivity() {

    // Создаём контейнер с зависимостями приложения
    private val appContainer by lazy {
        AppContainer(applicationContext)
    }

    // Создаём ViewModel для главного экрана
    private val breedViewModel: BreedViewModel by viewModels {
        BreedViewModelFactory(appContainer.breedRepository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Разрешаем интерфейсу использовать область за системными панелями
        enableEdgeToEdge()

        setContent {

            DogbreedsgalleryTheme {

                // Получаем актуальное состояние интерфейса
                val uiState by breedViewModel.uiState.collectAsStateWithLifecycle()

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    // Отображаем главный экран каталога
                    HomeScreen(
                        uiState = uiState,

                        onSearchQueryChange = breedViewModel::onSearchQueryChange,

                        onBreedClick = { breedId ->
                            // Переход к подробной информации подключим через Navigation 3
                        },

                        onFavoriteClick = breedViewModel::toggleFavorite,

                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


