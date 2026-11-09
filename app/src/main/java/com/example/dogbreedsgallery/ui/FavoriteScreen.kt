package com.example.dogbreedsgallery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dogbreedsgallery.data.local.BreedEntity

@Composable
fun FavoriteScreen(
    breeds: List<BreedEntity>,
    onBreedClick: (Int) -> Unit,
    onFavoriteClick: (Int, Boolean) -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF9F4))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 16.dp, top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onBackClick
            ) {
                Text(
                    text = "← Назад",
                    color = Color(0xFF9B7058)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "♥",
                color = Color(0xFFE47777),
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Text(
            text = "Избранное",
            modifier = Modifier.padding(
                start = 20.dp,
                end = 20.dp,
                bottom = 6.dp
            ),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF40332C)
        )

        Text(
            text = "Породы, которые тебе понравились",
            modifier = Modifier.padding(
                start = 20.dp,
                end = 20.dp,
                bottom = 16.dp
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF8A7A70)
        )

        if (breeds.isEmpty()) {
            Text(
                text = "Здесь пока ничего нет.\nДобавь понравившиеся породы в избранное!",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(24.dp),
                color = Color(0xFF8A7A70),
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 155.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 14.dp,
                    end = 14.dp,
                    top = 4.dp,
                    bottom = 20.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = breeds,
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