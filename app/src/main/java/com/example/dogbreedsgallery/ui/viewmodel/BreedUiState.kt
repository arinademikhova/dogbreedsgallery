
package com.example.dogbreedsgallery.ui.viewmodel

import com.example.dogbreedsgallery.data.local.entity.BreedEntity

data class BreedUiState(

    // Список пород, отображаемых на экране
    val breeds: List<BreedEntity> = emptyList(),

    // Поисковый запрос пользователя
    val searchQuery: String = "",

    // Состояние загрузки
    val isLoading: Boolean = false,

    // Сообщение об ошибке
    val errorMessage: String? = null
)
