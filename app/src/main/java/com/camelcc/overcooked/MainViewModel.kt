package com.camelcc.overcooked

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModelFactory(private val appContext: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            val dispatchers = AppDispatchers(
                io = Dispatchers.IO,
                compute = Dispatchers.Default,
                main = Dispatchers.Main)
            return MainViewModel(appContext, dispatchers) as T
        } else {
            throw IllegalArgumentException("ViewModel not found")
        }
    }
}

data class AppDispatchers(
    val io: CoroutineDispatcher,
    val compute: CoroutineDispatcher,
    val main: CoroutineDispatcher
)

sealed class LoadingState {
    object None: LoadingState()
    object Loading: LoadingState()
    class Failed(val error: Error?): LoadingState()
}

class MainViewModel(
    appContext: Context,
    private val dispatchers: AppDispatchers) : ViewModel() {
    private val _loadingFlow = MutableStateFlow<LoadingState>(LoadingState.None)
    val loadingFlow = _loadingFlow.asStateFlow()

    private var originPhotos = listOf<Photo>()
    private var displayedPhotos = listOf<Photo>()
    private val _photosFlow = MutableStateFlow(listOf<Photo>())
    val photosFlow = _photosFlow.asStateFlow()

    private var selectedPhoto: Photo? = null

    private var visibleItems = hashSetOf<Long>()

    private val repository = Repository(appContext)

    init {
        refresh()
        viewModelScope.launch {
            withContext(dispatchers.compute) {
                while (true) {
                    delay(1000L)
                    val newPhotos = displayedPhotos.map { photo ->
                        if (visibleItems.contains(photo.id) && photo.enabled) {
                            photo.copy(counter = photo.counter+1)
                        } else {
                            photo
                        }
                    }
                    displayedPhotos = newPhotos
                    _photosFlow.update { newPhotos }
                }
            }
        }

        viewModelScope.launch {
            withContext(dispatchers.compute) {
                repository.photos().collect { photos ->
                    displayedPhotos = photos.map { it.toPhoto() }
                    _photosFlow.update { displayedPhotos }
                }
            }
        }
    }

    fun search(search: String) {
        viewModelScope.launch {
            withContext(dispatchers.compute) {
                _loadingFlow.update { LoadingState.Loading }
                val res = repository.searchPhotos(search)
                if (res.isSuccessful) {
                    val photos = res.body()?.photos?.map { it.toPhoto() }
                    if (photos != null) {
                        originPhotos = displayedPhotos
                        displayedPhotos = photos
                        _photosFlow.update { displayedPhotos }
                    }
                }
                _loadingFlow.update { LoadingState.None }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            withContext(dispatchers.compute) {
                _loadingFlow.update { LoadingState.Loading }
                repository.refreshPhotos()
                _loadingFlow.update { LoadingState.None }
            }
        }
    }

    fun selectPhoto(photo: Photo) {
        selectedPhoto = photo
    }

    fun togglePhotoButton(photo: Photo) {
        val newPhotos = displayedPhotos.map {
            if (photo.id != it.id) it else photo.copy(enabled = !it.enabled)
        }
        displayedPhotos = newPhotos
        _photosFlow.update { newPhotos }
    }

    fun updateVisiblePhotos(photos: List<Photo>) {
        visibleItems = photos.map { it.id }.toHashSet()
    }

    fun resetSearch() {
        displayedPhotos = originPhotos
        originPhotos = listOf()
        _photosFlow.update { displayedPhotos }
    }
}