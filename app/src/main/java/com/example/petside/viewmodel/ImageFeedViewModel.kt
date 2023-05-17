package com.example.petside.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petside.data.db.UserEntity
import com.example.petside.data.model.CatImage
import com.example.petside.data.model.FavouriteImage
import com.example.petside.data.repository.UserRepository
import com.example.petside.data.retrofit.FavouritesRequest
import com.example.petside.data.retrofit.RetrofitService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

data class FeedUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
)

class ImageFeedViewModel : ViewModel() {

    @Inject
    lateinit var retrofitService: RetrofitService

    @Inject
    lateinit var userRepository: UserRepository

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    var initialized = false
    var apiKey: String = ""
    private var limit = 10
    private var page = 0
    var hasMore = true

    lateinit var user: LiveData<UserEntity>

    var liveFeedList = MutableLiveData<List<CatImage>>()
    private var feedList = ArrayList<CatImage>()

    //TODO Перенести в другую ВьюМодель
    var liveFavouritesList = MutableLiveData<List<FavouriteImage>>()
    private var favouritesList = ArrayList<FavouriteImage>()

    fun getUser() {
        viewModelScope.launch {
            user = userRepository.getUser()
        }
    }

    fun initialize(onSuccess: () -> Unit) {
        if (!initialized) {
            getFavourites(onSuccess)
        }
    }

    fun getNextPage(reload: Boolean = false) {
        if (reload) {
            page = 0
            feedList.clear()
            hasMore = true
            liveFeedList.value = feedList
            _uiState.update { uiState -> uiState.copy(isLoading = true) }
        } else {
            _uiState.update { uiState -> uiState.copy(isLoadingMore = true) }
        }
        if (hasMore) {
            viewModelScope.launch {
                try {
                    val newList = retrofitService.getCatImages(
                        apiKey, limit, page
                    )
                    newList.forEach { catImage ->
                        val favouriteImage = favouritesList.find { favouriteImage ->
                            catImage.id == favouriteImage.image.id
                        }
                        if (favouriteImage !== null) {
                            catImage.favourite = favouriteImage.id
                        }
                    }
                    feedList.addAll(newList)
                    liveFeedList.value = feedList
                    page++
                    hasMore = newList.size == 10
                    _uiState.update { uiState ->
                        uiState.copy(
                            isLoading = false, isLoadingMore = false
                        )
                    }
                } catch (e: HttpException) {
                    _uiState.update { uiState -> uiState.copy(errorMessage = e.message()) }
                }
            }
        }
    }

    fun addToFavourites(index: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val image = feedList[index]
                val favouriteOnlyId: FavouriteImage =
                    retrofitService.addToFavourites(apiKey, FavouritesRequest(image_id = image.id))
                val favouriteImage = FavouriteImage(id = favouriteOnlyId.id, image)
                favouritesList.add(favouriteImage)
                liveFavouritesList.value = favouritesList
                image.favourite = favouriteOnlyId.id
                liveFeedList.value = feedList
                onSuccess()
            } catch (e: HttpException) {
                _uiState.update { uiState -> uiState.copy(errorMessage = e.message()) }
            }
        }
    }

    fun deleteFromFavourites(
        index: Int, onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val image = feedList[index]
                image.favourite?.let {
                    retrofitService.deleteFromFavourites(apiKey, it)
                    favouritesList.remove(FavouriteImage(id = it, image = image))
                }
                image.favourite = null
                liveFeedList.value = feedList
                liveFavouritesList.value = favouritesList
                onSuccess()
            } catch (e: HttpException) {
                _uiState.update { uiState -> uiState.copy(errorMessage = e.message()) }
            }
        }
    }

    private fun getFavourites(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                favouritesList = retrofitService.getFavourites(apiKey)
                liveFavouritesList.value = favouritesList
                initialized = true
                onSuccess()
            } catch (e: HttpException) {
                _uiState.update { uiState -> uiState.copy(errorMessage = e.message()) }
            }
        }
    }

    fun clearError() {
        _uiState.update { currentUiState ->
            currentUiState.copy(errorMessage = null)
        }
    }

}