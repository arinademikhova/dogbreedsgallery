
package com.example.dogbreedsgallery.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.dogbreedsgallery.ui.components.BreedCard
import com.example.dogbreedsgallery.ui.viewmodel.BreedUiState


@Composable
fun HomeScreen(
    uiState: BreedUiState,
    onSearchQueryChange: (String) -> Unit,
    onBreedClick: (Int) -> Unit,
    onFavoriteClick: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        // Заголовок
        Text(
            text = "Dog Breeds Gallery",
            modifier = Modifier.padding(16.dp)
        )

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
                    modifier = Modifier.align(Alignment.CenterHorizontally)
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
