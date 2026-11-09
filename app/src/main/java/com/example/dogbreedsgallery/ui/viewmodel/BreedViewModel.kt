package com.example.dogbreedsgallery.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dogbreedsgallery.data.local.BreedEntity
import com.example.dogbreedsgallery.data.repository.BreedRepository

import com.example.dogbreedsgallery.DogImageDto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi

// Состояние главного экрана
data class BreedUiState(
    val breeds: List<BreedEntity> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// ViewModel — управление данными экранов
class BreedViewModel(
    private val repository: BreedRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val isLoading = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)

    // ID выбранной породы
    private val selectedBreedId = MutableStateFlow<Int?>(null)

    // Фотографии выбранной породы
    private val _breedImages = MutableStateFlow<List<String>>(emptyList())
    val breedImages: StateFlow<List<String>> = _breedImages.asStateFlow()

    // Состояние загрузки фотографий
    private val _isImagesLoading = MutableStateFlow(false)
    val isImagesLoading: StateFlow<Boolean> = _isImagesLoading.asStateFlow()

    // Ошибка загрузки фотографий
    private val _imagesError = MutableStateFlow<String?>(null)
    val imagesError: StateFlow<String?> = _imagesError.asStateFlow()

    // Получение всех пород из Room
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
    @OptIn(ExperimentalCoroutinesApi::class)

    // Получение выбранной породы
    val selectedBreed: StateFlow<BreedEntity?> =
        selectedBreedId
            .flatMapLatest { id ->
                if (id == null) {
                    flowOf(null)
                } else {
                    repository.getBreedById(id)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    // Объединение данных для главного экрана
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

    init {
        refreshBreeds()
    }

    // Первоначальная загрузка и обновление пород
    fun refreshBreeds() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                repository.refreshBreeds()
            } catch (exception: Exception) {
                if (exception is CancellationException) throw exception

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

    // Добавление породы в избранное или удаление из него
    fun toggleFavorite(breedId: Int, favorite: Boolean) {
        viewModelScope.launch {
            repository.updateFavorite(breedId, favorite)
        }
    }

    // Открытие подробностей породы и загрузка её фотографий
    fun openBreed(breedId: Int) {
        selectedBreedId.value = breedId

        viewModelScope.launch {
            _isImagesLoading.value = true
            _imagesError.value = null
            _breedImages.value = emptyList()

            try {
                val images: List<DogImageDto> =
                    repository.getBreedImages(breedId)

                _breedImages.value = images
                    .mapNotNull { it.url }
                    .filter { it.isNotBlank() }
                    .distinct()

            } catch (exception: Exception) {
                if (exception is CancellationException) throw exception

                _imagesError.value =
                    exception.message ?: "Не удалось загрузить фотографии"
            } finally {
                _isImagesLoading.value = false
            }
        }
    }

    // Закрытие подробностей
    fun closeBreed() {
        selectedBreedId.value = null
        _breedImages.value = emptyList()
        _imagesError.value = null
    }

    // Очистка ошибки главного экрана
    fun clearError() {
        errorMessage.value = null
    }
}

// Фабрика для создания ViewModel
class BreedViewModelFactory(
    private val repository: BreedRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BreedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BreedViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}