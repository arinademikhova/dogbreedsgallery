package com.example.dogbreedsgallery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.CachePolicy
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.example.dogbreedsgallery.data.local.BreedEntity
import com.example.dogbreedsgallery.ui.viewmodel.BreedUiState


// Заглушка для фотографии
@Composable
private fun ImagePlaceholder(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0xFFF1E7DF)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🐶",
            style = MaterialTheme.typography.displayMedium
        )
    }
}

// Карточка породы
@Composable
fun BreedCard(
    breed: BreedEntity,
    onClick: () -> Unit,
    onFavoriteClick: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val imageRequest = ImageRequest.Builder(context)
        .data(breed.imageUrl.takeIf { it.isNotBlank() })
        .memoryCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .build()

    Card(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            if (breed.imageUrl.isBlank()) {
                ImagePlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
            } else {
                SubcomposeAsyncImage(
                    model = imageRequest,
                    contentDescription = breed.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Crop,
                    loading = {
                        ImagePlaceholder(
                            modifier = Modifier.fillMaxSize()
                        )
                    },
                    error = {
                        ImagePlaceholder(
                            modifier = Modifier.fillMaxSize()
                        )
                    },
                    success = {
                        SubcomposeAsyncImageContent()
                    }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = breed.name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = breed.temperament.ifBlank {
                            "Характер не указан"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2
                    )
                }

                IconButton(
                    onClick = {
                        onFavoriteClick(
                            breed.id,
                            !breed.isFavorite
                        )
                    }
                ) {
                    Text(
                        text = if (breed.isFavorite) "♥" else "♡",
                        color = Color(0xFFE47777)
                    )
                }
            }
        }
    }
}

// Главный экран
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: BreedUiState,
    onSearchQueryChange: (String) -> Unit,
    onBreedClick: (Int) -> Unit,
    onFavoriteClick: (Int, Boolean) -> Unit,
    onFavoritesClick: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {

        Text(
            text = "Dog Breeds Gallery",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        TextButton(
            onClick = onFavoritesClick,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text("♥ Избранное")
        }

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

        uiState.errorMessage?.let { error ->
            Text(
                text = error,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.error
            )
        }

        // Обновление списка свайпом вниз
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = onRefresh,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                if (uiState.breeds.isEmpty()) {

                    item(
                        span = {
                            GridItemSpan(maxLineSpan)
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                uiState.isLoading -> {
                                    CircularProgressIndicator()
                                }

                                uiState.errorMessage != null -> {
                                    Text("Не удалось загрузить породы")
                                }

                                else -> {
                                    Text("Породы не найдены")
                                }
                            }
                        }
                    }

                } else {

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
}