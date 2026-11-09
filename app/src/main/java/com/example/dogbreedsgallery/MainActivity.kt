package com.example.dogbreedsgallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.dogbreedsgallery.di.AppContainer
import com.example.dogbreedsgallery.ui.BreedDetailsScreen
import com.example.dogbreedsgallery.ui.FavoriteScreen
import com.example.dogbreedsgallery.ui.HomeScreen
import com.example.dogbreedsgallery.ui.theme.DogbreedsgalleryTheme
import com.example.dogbreedsgallery.ui.viewmodel.BreedViewModel
import com.example.dogbreedsgallery.ui.viewmodel.BreedViewModelFactory

private data object HomeRoute
private data object FavoritesRoute
private data class DetailsRoute(val breedId: Int)

class MainActivity : ComponentActivity() {

    private val appContainer by lazy {
        AppContainer(applicationContext)
    }

    private val breedViewModel: BreedViewModel by viewModels {
        BreedViewModelFactory(appContainer.breedRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DogbreedsgalleryTheme {

                val uiState by breedViewModel.uiState.collectAsStateWithLifecycle()
                val favoriteBreeds by breedViewModel.favoriteBreeds.collectAsStateWithLifecycle()
                val selectedBreed by breedViewModel.selectedBreed.collectAsStateWithLifecycle()
                val breedImages by breedViewModel.breedImages.collectAsStateWithLifecycle()
                val isImagesLoading by breedViewModel.isImagesLoading.collectAsStateWithLifecycle()
                val imagesError by breedViewModel.imagesError.collectAsStateWithLifecycle()

                // Стек экранов Navigation 3
                val backStack = remember {
                    mutableStateListOf<Any>(HomeRoute)
                }

                val currentRoute = backStack.lastOrNull()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),

                    // Нижняя панель отображается только на основных экранах
                    bottomBar = {
                        if (currentRoute == HomeRoute ||
                            currentRoute == FavoritesRoute
                        ) {
                            NavigationBar {

                                NavigationBarItem(
                                    selected = currentRoute == HomeRoute,
                                    onClick = {
                                        backStack.clear()
                                        backStack.add(HomeRoute)
                                    },
                                    icon = {
                                        Text("⌂")
                                    },
                                    label = {
                                        Text("Главная")
                                    }
                                )

                                NavigationBarItem(
                                    selected = currentRoute == FavoritesRoute,
                                    onClick = {
                                        backStack.clear()
                                        backStack.add(FavoritesRoute)
                                    },
                                    icon = {
                                        Text("♥")
                                    },
                                    label = {
                                        Text("Избранное")
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->

                    NavDisplay(
                        backStack = backStack,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),

                        onBack = {
                            if (backStack.size > 1) {
                                if (backStack.lastOrNull() is DetailsRoute) {
                                    breedViewModel.closeBreed()
                                }

                                backStack.removeLastOrNull()
                            }
                        },

                        entryProvider = { key ->

                            when (key) {

                                // Главная
                                HomeRoute -> NavEntry(key) {
                                    HomeScreen(
                                        uiState = uiState,
                                        onSearchQueryChange = breedViewModel::onSearchQueryChange,
                                        onBreedClick = { breedId ->
                                            breedViewModel.openBreed(breedId)
                                            backStack.add(DetailsRoute(breedId))
                                        },
                                        onFavoriteClick = breedViewModel::toggleFavorite,
                                        onFavoritesClick = {
                                            backStack.add(FavoritesRoute)
                                        },
                                        onRefresh = breedViewModel::refreshBreeds
                                    )
                                }

                                // Избранное
                                FavoritesRoute -> NavEntry(key) {
                                    FavoriteScreen(
                                        breeds = favoriteBreeds,
                                        onBreedClick = { breedId ->
                                            breedViewModel.openBreed(breedId)
                                            backStack.add(DetailsRoute(breedId))
                                        },
                                        onFavoriteClick = breedViewModel::toggleFavorite,
                                        onBackClick = {
                                            backStack.clear()
                                            backStack.add(HomeRoute)
                                        }
                                    )
                                }

                                // Подробности породы
                                is DetailsRoute -> NavEntry(key) {

                                    if (selectedBreed != null &&
                                        selectedBreed?.id == key.breedId
                                    ) {
                                        BreedDetailsScreen(
                                            breed = selectedBreed!!,
                                            images = breedImages,
                                            isImagesLoading = isImagesLoading,
                                            imagesError = imagesError,
                                            onBackClick = {
                                                breedViewModel.closeBreed()
                                                backStack.removeLastOrNull()
                                            },
                                            onFavoriteClick = breedViewModel::toggleFavorite
                                        )
                                    } else {
                                        Text("Загружаем информацию о породе...")
                                    }
                                }

                                else -> NavEntry(key) {
                                    Text("Экран не найден")
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}