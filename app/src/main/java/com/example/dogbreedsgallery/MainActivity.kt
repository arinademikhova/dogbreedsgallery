package com.example.dogbreedsgallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dogbreedsgallery.di.AppContainer
import com.example.dogbreedsgallery.ui.BreedDetailsScreen
import com.example.dogbreedsgallery.ui.FavoriteScreen
import com.example.dogbreedsgallery.ui.HomeScreen
import com.example.dogbreedsgallery.ui.theme.DogbreedsgalleryTheme
import com.example.dogbreedsgallery.ui.viewmodel.BreedViewModel
import com.example.dogbreedsgallery.ui.viewmodel.BreedViewModelFactory

// Три экрана приложения
private enum class AppScreen {
    Home,
    Details,
    Favorites
}

class MainActivity : ComponentActivity() {

    // Создаём контейнер с зависимостями
    private val appContainer by lazy {
        AppContainer(applicationContext)
    }

    // Создаём ViewModel
    private val breedViewModel: BreedViewModel by viewModels {
        BreedViewModelFactory(appContainer.breedRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            DogbreedsgalleryTheme {

                // Получаем данные для экранов
                val uiState by breedViewModel.uiState.collectAsStateWithLifecycle()
                val favoriteBreeds by breedViewModel.favoriteBreeds.collectAsStateWithLifecycle()
                val selectedBreed by breedViewModel.selectedBreed.collectAsStateWithLifecycle()
                val breedImages by breedViewModel.breedImages.collectAsStateWithLifecycle()
                val isImagesLoading by breedViewModel.isImagesLoading.collectAsStateWithLifecycle()
                val imagesError by breedViewModel.imagesError.collectAsStateWithLifecycle()

                // Текущий и предыдущий экраны
                var currentScreen by remember {
                    mutableStateOf(AppScreen.Home)
                }

                var previousScreen by remember {
                    mutableStateOf(AppScreen.Home)
                }

                // Обработка системной кнопки "Назад"
                BackHandler(
                    enabled = currentScreen != AppScreen.Home
                ) {
                    if (currentScreen == AppScreen.Details) {
                        breedViewModel.closeBreed()
                    }

                    currentScreen = if (currentScreen == AppScreen.Details) {
                        previousScreen
                    } else {
                        AppScreen.Home
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {

                        when (currentScreen) {

                            // Главный экран
                            AppScreen.Home -> {
                                HomeScreen(
                                    uiState = uiState,
                                    onSearchQueryChange = breedViewModel::onSearchQueryChange,

                                    onBreedClick = { breedId ->
                                        previousScreen = AppScreen.Home
                                        breedViewModel.openBreed(breedId)
                                        currentScreen = AppScreen.Details
                                    },

                                    onFavoriteClick = breedViewModel::toggleFavorite,

                                    onFavoritesClick = {
                                        currentScreen = AppScreen.Favorites
                                    }
                                )
                            }

                            // Подробности породы
                            AppScreen.Details -> {
                                if (selectedBreed != null) {
                                    BreedDetailsScreen(
                                        breed = selectedBreed!!,
                                        images = breedImages,
                                        isImagesLoading = isImagesLoading,
                                        imagesError = imagesError,

                                        onBackClick = {
                                            breedViewModel.closeBreed()
                                            currentScreen = previousScreen
                                        },

                                        onFavoriteClick = breedViewModel::toggleFavorite
                                    )
                                } else {
                                    Text(
                                        text = "Загружаем информацию о породе...",
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }

                            // Избранное
                            AppScreen.Favorites -> {
                                FavoriteScreen(
                                    breeds = favoriteBreeds,

                                    onBreedClick = { breedId ->
                                        previousScreen = AppScreen.Favorites
                                        breedViewModel.openBreed(breedId)
                                        currentScreen = AppScreen.Details
                                    },

                                    onFavoriteClick = breedViewModel::toggleFavorite,

                                    onBackClick = {
                                        currentScreen = AppScreen.Home
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}