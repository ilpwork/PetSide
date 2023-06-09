package com.example.petside.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.domain.model.CatImage
import com.example.domain.model.FavouriteImage
import com.example.domain.model.User
import com.example.domain.usecase.GetUserUseCase
import com.example.petside.data.repository.CatImageRepository
import com.example.petside.data.retrofit.FavouritesRequest
import com.example.petside.data.retrofit.RetrofitService
import kotlinx.coroutines.flow.Flow
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
    lateinit var getUserUseCase: GetUserUseCase

    @Inject
    lateinit var catImageRepository: CatImageRepository

    var list: Flow<PagingData<CatImage>> =
        Pager(config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { catImageRepository.getPagingSource() }).flow.cachedIn(
                viewModelScope
            )

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    var initialized = false
    var apiKey: String = ""

    lateinit var user: LiveData<User>

    var liveFeedList = MutableLiveData<List<CatImage>>()
    private var feedList = ArrayList<CatImage>()

    //TODO Перенести в другую ВьюМодель
    var liveFavouritesList = MutableLiveData<List<FavouriteImage>>()
    private var favouritesList = ArrayList<FavouriteImage>()

    fun getUser() {
        viewModelScope.launch {
            user = getUserUseCase.execute()
        }
    }

    fun initialize() {
        if (!initialized) {
            getFavourites()
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

    private fun getFavourites() {
        viewModelScope.launch {
            try {
                favouritesList = retrofitService.getFavourites(apiKey)
                liveFavouritesList.value = favouritesList
                initialized = true
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