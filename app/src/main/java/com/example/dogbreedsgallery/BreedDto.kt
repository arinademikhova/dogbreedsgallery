package com.example.dogbreedsgallery

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Ответ для /breeds (одна порода из списка)
 */
@JsonClass(generateAdapter = true)
data class BreedDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "temperament") val temperament: String? = null,
    @Json(name = "origin") val origin: String? = null,
    @Json(name = "life_span") val lifeSpan: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "image") val image: BreedImageDto? = null
)

/**
 * Вложенный объект картинки внутри BreedDto
 */
@JsonClass(generateAdapter = true)
data class BreedImageDto(
    @Json(name = "id") val id: String?,
    @Json(name = "url") val url: String?
)

/**
 * Ответ для /images/search
 */
@JsonClass(generateAdapter = true)
data class DogImageDto(
    @Json(name = "id") val id: String,
    @Json(name = "url") val url: String
)