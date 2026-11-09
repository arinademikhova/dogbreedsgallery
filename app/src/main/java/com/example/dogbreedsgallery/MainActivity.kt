
package com.example.dogbreedsgallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import com.example.dogbreedsgallery.di.AppContainer
import com.example.dogbreedsgallery.ui.screens.home.HomeScreen
import com.example.dogbreedsgallery.ui.theme.DogbreedsgalleryTheme
import com.example.dogbreedsgallery.ui.viewmodel.BreedViewModel
import com.example.dogbreedsgallery.ui.viewmodel.BreedViewModelFactory

class MainActivity : ComponentActivity() {

    private val appContainer by lazy {
        AppContainer(applicationContext)
    }

    private val breedViewModel: BreedViewModel by viewModels {
        BreedViewModelFactory(appContainer.breedRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DogbreedsgalleryTheme {

                val uiState by breedViewModel.uiState.collectAsStateWithLifecycle()

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    HomeScreen(
                        uiState = uiState,
                        onSearchQueryChange = breedViewModel::onSearchQueryChange,
                        onBreedClick = { _ ->
                            // Подробный экран подключим следующим этапом
                        },
                        onFavoriteClick = breedViewModel::toggleFavorite,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
