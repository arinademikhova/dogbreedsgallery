
package com.example.dogbreedsgallery.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable

import coil.compose.AsyncImage

import com.example.dogbreedsgallery.data.local.entity.BreedEntity


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
                model = breed.imageUrl.takeIf { it.isNotBlank() },
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
