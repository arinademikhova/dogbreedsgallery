package com.example.dogbreedsgallery.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items

import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

import coil.compose.AsyncImage

import com.example.dogbreedsgallery.data.local.BreedEntity
import com.example.dogbreedsgallery.ui.viewmodel.BreedUiState

import androidx.compose.material3.TextButton

// 1. Карточка породы собаки

@Composable
fun BreedCard(
    breed: BreedEntity,
    onClick: () -> Unit,
    onFavoriteClick: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    Card(
        onClick = onClick,
        modifier = modifier
    ) {

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            // Фотография породы
            AsyncImage(
                model = breed.imageUrl.takeIf {
                    it.isNotBlank()
                },
                contentDescription = breed.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )


            // Название и кнопка избранного
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    // Название породы
                    Text(
                        text = breed.name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    // Характер породы
                    Text(
                        text = breed.temperament.ifBlank {
                            "Характер не указан"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2
                    )
                }


                // Кнопка избранного
                IconButton(
                    onClick = {
                        onFavoriteClick(
                            breed.id,
                            !breed.isFavorite
                        )
                    }
                ) {

                    Text(
                        text = if (breed.isFavorite) "♥" else "♡"
                    )
                }
            }
        }
    }
}

// 2. Главный экран приложения

@Composable
fun HomeScreen(
    uiState: BreedUiState,
    onSearchQueryChange: (String) -> Unit,
    onBreedClick: (Int) -> Unit,
    onFavoriteClick: (Int, Boolean) -> Unit,
    onFavoritesClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        // Заголовок
        Text(
            text = "Dog Breeds Gallery",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        TextButton(
            onClick = onFavoritesClick,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text("♥ Избранное")
        }

        // Поисковая строка
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchQueryChange,
            label = {
                Text("Поиск породы")
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )


        Spacer(modifier = Modifier.height(12.dp))


        // Индикатор загрузки
        if (uiState.isLoading) {

            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }


        // Сообщение об ошибке
        uiState.errorMessage?.let { error ->

            Text(
                text = error,
                modifier = Modifier.padding(16.dp)
            )
        }


        // Список пород
        if (uiState.breeds.isEmpty()) {

            if (uiState.isLoading) {

                CircularProgressIndicator(
                    modifier = Modifier.align(
                        Alignment.CenterHorizontally
                    )
                )

            } else if (uiState.errorMessage == null) {

                Text(
                    text = "Породы не найдены",
                    modifier = Modifier.padding(16.dp)
                )
            }

        } else {

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(
                    items = uiState.breeds,
                    key = { breed -> breed.id }
                ) { breed ->

                    BreedCard(
                        breed = breed,

                        onClick = {
                            onBreedClick(breed.id)
                        },

                        onFavoriteClick = onFavoriteClick
                    )
                }
            }
        }
    }
}