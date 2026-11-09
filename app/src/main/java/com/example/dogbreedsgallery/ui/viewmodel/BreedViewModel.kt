package com.example.dogbreedsgallery.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

import com.example.dogbreedsgallery.data.local.BreedEntity
import com.example.dogbreedsgallery.data.repository.BreedRepository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


// 1. Состояние интерфейса

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

// 2. ViewModel — управление данными интерфейса

class BreedViewModel(
    private val repository: BreedRepository
) : ViewModel() {

    // Поисковый запрос
    private val searchQuery = MutableStateFlow("")

    // Состояние загрузки
    private val isLoading = MutableStateFlow(false)

    // Сообщение об ошибке
    private val errorMessage = MutableStateFlow<String?>(null)


    // Получение всех пород из Repository
    private val allBreeds: StateFlow<List<BreedEntity>> =
        repository.getBreeds().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    // Получение избранных пород
    val favoriteBreeds: StateFlow<List<BreedEntity>> =
        repository.getFavoriteBreeds().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    // Объединение данных в единое состояние интерфейса
    val uiState: StateFlow<BreedUiState> =
        combine(
            allBreeds,
            searchQuery,
            isLoading,
            errorMessage
        ) { breeds, query, loading, error ->

            val filteredBreeds = if (query.isBlank()) {
                breeds
            } else {
                breeds.filter {
                    it.name.contains(query, ignoreCase = true)
                }
            }

            BreedUiState(
                breeds = filteredBreeds,
                searchQuery = query,
                isLoading = loading,
                errorMessage = error
            )

        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BreedUiState()
        )


    // Первоначальная загрузка пород
    init {
        refreshBreeds()
    }


    // Обновление данных из интернета
    fun refreshBreeds() {

        viewModelScope.launch {

            isLoading.value = true
            errorMessage.value = null

            try {
                repository.refreshBreeds()

            } catch (exception: Exception) {

                if (exception is CancellationException) {
                    throw exception
                }

                errorMessage.value =
                    exception.message ?: "Не удалось загрузить породы"

            } finally {
                isLoading.value = false
            }
        }
    }


    // Изменение поискового запроса
    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }


    // Изменение статуса избранного
    fun toggleFavorite(breedId: Int, favorite: Boolean) {

        viewModelScope.launch {
            repository.updateFavorite(breedId, favorite)
        }
    }


    // Получение конкретной породы
    fun getBreedById(breedId: Int) =
        repository.getBreedById(breedId)


    // Очистка сообщения об ошибке
    fun clearError() {
        errorMessage.value = null
    }
}

// 3. Фабрика для создания ViewModel

class BreedViewModelFactory(
    private val repository: BreedRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(BreedViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return BreedViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}