
package com.example.dogbreedsgallery.data.remote

import com.squareup.moshi.Json


// Информация о породе, полученная от сервера
data class BreedDto(
    val id: Int,
    val name: String,
    val temperament: String?,
    val origin: String?,

    @Json(name = "life_span")
    val lifeSpan: String?,

    val description: String?,
    val image: BreedImageDto?
)


// Фотография, которая приходит вместе с породой
data class BreedImageDto(
    val id: String?,
    val url: String?
)


// Отдельная фотография из запроса images/search
data class DogImageDto(
    val id: String?,
    val url: String?
)
