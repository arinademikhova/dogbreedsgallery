package com.example.dogbreedsgallery.data.remote.dto

import com.squareup.moshi.Json

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

data class BreedImageDto(
    val id: String?,
    val url: String?
)