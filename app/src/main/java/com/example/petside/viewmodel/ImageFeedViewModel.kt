package com.example.petside.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petside.model.CatImage
import com.example.petside.model.FavouriteImage
import com.example.petside.retrofit.FavouritesRequest
import com.example.petside.retrofit.RetrofitService
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ImageFeedViewModel : ViewModel() {

    lateinit var retrofitService: RetrofitService
    var apiKey: String = ""
    private var limit = 10
    private var page = 0
    var loading = true
    var loadingMore = false
    var hasMore = true

    var liveFeedList = MutableLiveData<List<CatImage>>()
    private var feedList = ArrayList<CatImage>()

    //TODO Перенести в другую ВьюМодель
    var liveFavouritesList = MutableLiveData<List<FavouriteImage>>()
    private var favouritesList = ArrayList<FavouriteImage>()

    fun initialize(onSuccess: () -> Unit, onError: (e: HttpException) -> Unit) {
        getFavourites(onSuccess, onError)
    }

    fun getNextPage(reload: Boolean = false, onError: (e: HttpException) -> Unit) {
        if (reload) {
            page = 0
            feedList.clear()
            hasMore = true
            liveFeedList.value = feedList
            loading = true
        } else {
            loadingMore = true
        }
        if (hasMore) {
            viewModelScope.launch {
                try {
                    val newList = retrofitService.getCatImages(
                        apiKey,
                        limit,
                        page
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
                    loading = false
                    loadingMore = false
                } catch (e: HttpException) {
                    onError(e)
                }
            }
        }
    }

    fun addToFavourites(index: Int, onSuccess: () -> Unit, onError: (e: HttpException) -> Unit) {
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
                onError(e)
            }
        }
    }

    fun deleteFromFavourites(
        index: Int,
        onSuccess: () -> Unit,
        onError: (e: HttpException) -> Unit
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
                onError(e)
            }
        }
    }

    private fun getFavourites(onSuccess: () -> Unit, onError: (e: HttpException) -> Unit) {
        viewModelScope.launch {
            try {
                favouritesList = retrofitService.getFavourites(apiKey)
                liveFavouritesList.value = favouritesList
                onSuccess()
            } catch (e: HttpException) {
                onError(e)
            }
        }
    }


}