package com.example.dogbreedsgallery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.dogbreedsgallery.data.local.BreedEntity

@Composable
private fun DetailsImagePlaceholder(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    Box(
        modifier = modifier
            .background(Color(0xFFF1E7DF)),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color(0xFF9B7058)
            )
        } else {
            Text(
                text = "🐶",
                style = MaterialTheme.typography.displayLarge
            )
        }
    }
}

@Composable
fun BreedDetailsScreen(
    breed: BreedEntity,
    images: List<String>,
    isImagesLoading: Boolean,
    imagesError: String?,
    onBackClick: () -> Unit,
    onFavoriteClick: (Int, Boolean) -> Unit
) {
    val galleryImages = remember(breed.id, breed.imageUrl, images) {
        (listOf(breed.imageUrl) + images)
            .filter { it.isNotBlank() }
            .distinct()
    }

    val pagerState = rememberPagerState(
        pageCount = {
            galleryImages.size
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF9F4))
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {

        // Кнопка возврата
        TextButton(
            onClick = onBackClick,
            modifier = Modifier.padding(start = 8.dp, top = 8.dp)
        ) {
            Text(
                text = "← Назад",
                color = Color(0xFF9B7058)
            )
        }

        // Название породы
        Text(
            text = breed.name,
            modifier = Modifier.padding(horizontal = 20.dp),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF40332C)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Галерея фотографий
        if (galleryImages.isNotEmpty()) {

            val context = LocalContext.current

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) { page ->

                val imageUrl = galleryImages[page]

                val imageRequest = remember(imageUrl) {
                    ImageRequest.Builder(context)
                        .data(imageUrl)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .build()
                }

                SubcomposeAsyncImage(
                    model = imageRequest,
                    contentDescription = "Фотография породы ${breed.name}",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentScale = ContentScale.Crop,
                    loading = {
                        DetailsImagePlaceholder(
                            modifier = Modifier.fillMaxSize(),
                            isLoading = true
                        )
                    },
                    error = {
                        DetailsImagePlaceholder(
                            modifier = Modifier.fillMaxSize()
                        )
                    },
                    success = {
                        SubcomposeAsyncImageContent()
                    }
                )
            }

            // Номер текущей фотографии
            if (galleryImages.size > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${pagerState.currentPage + 1} / ${galleryImages.size}",
                        color = Color(0xFF8A7A70),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

        } else {

            // Если фотографий нет, показываем заглушку
            DetailsImagePlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(horizontal = 16.dp),
                isLoading = isImagesLoading
            )
        }

        if (isImagesLoading && galleryImages.isEmpty()) {
            Text(
                text = "Загружаем фотографии...",
                modifier = Modifier.padding(20.dp),
                color = Color(0xFF8A7A70)
            )
        }

        imagesError?.let { error ->
            Text(
                text = error,
                modifier = Modifier.padding(horizontal = 20.dp),
                color = MaterialTheme.colorScheme.error
            )
        }

        // Основная информация
        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = "О породе",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF40332C)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = breed.description
                    ?.takeIf { it.isNotBlank() }
                    ?: "Описание этой породы пока отсутствует.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF66564D)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Характеристики",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF40332C)
            )

            Spacer(modifier = Modifier.height(10.dp))

            DetailInfoCard(
                title = "Характер",
                value = breed.temperament.ifBlank {
                    "Не указан"
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            DetailInfoCard(
                title = "Происхождение",
                value = breed.origin
                    ?.takeIf { it.isNotBlank() }
                    ?: "Не указано"
            )

            Spacer(modifier = Modifier.height(8.dp))

            DetailInfoCard(
                title = "Продолжительность жизни",
                value = breed.lifeSpan.ifBlank {
                    "Не указана"
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Добавление в избранное
            Button(
                onClick = {
                    onFavoriteClick(
                        breed.id,
                        !breed.isFavorite
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9B7058)
                )
            ) {
                Text(
                    text = if (breed.isFavorite) {
                        "♥ Удалить из избранного"
                    } else {
                        "♡ Добавить в избранное"
                    }
                )
            }
        }
    }
}

@Composable
private fun DetailInfoCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF9B7058)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF40332C)
            )
        }
    }
}