package com.example.petside.viewmodel

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.petside.db.UserEntity
import com.example.petside.model.CatImage
import com.example.petside.model.FavouriteImage
import com.example.petside.retrofit.FavouritesRequest
import com.example.petside.retrofit.RetrofitService
import com.example.petside.view.AlertFragment
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ImageFeedViewModel @AssistedInject constructor(
    @Assisted val lifecycleOwner: LifecycleOwner,
    @Assisted val parentFragmentManager: FragmentManager,
    val user: LiveData<UserEntity>,
    private val retrofitService: RetrofitService
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            lifecycleOwner: LifecycleOwner,
            parentFragmentManager: FragmentManager
        ): ImageFeedViewModel
    }

    private lateinit var apiKey: String
    private var limit = 10
    private var page = 0
    var loading = false
    var hasMore = true

    var liveFeedList = MutableLiveData<List<CatImage>>()
    private var feedList = ArrayList<CatImage>()

    //TODO Перенести в другую ВьюМодель
    var liveFavouritesList = MutableLiveData<List<FavouriteImage>>()
    private var favouritesList = ArrayList<FavouriteImage>()

    init {
        user.observe(lifecycleOwner) {
            if (it !== null) {
                apiKey = it.api_key
                getFavourites()
            }
        }
    }

    fun getNextPage() {
        if (hasMore) {
            loading = true

            CoroutineScope(Dispatchers.Main).launch {
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
                } catch (e: HttpException) {
                    val dialog = AlertFragment(e.message(), ::endLoading)
                    dialog.show(parentFragmentManager, "getImagesPageError")
                }
            }
        }
    }


    fun endLoading() {

    }

    fun addToFavourites(index: Int, onSuccess: () -> Unit, onError: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
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
                val dialog = AlertFragment(e.message(), onError)
                dialog.show(parentFragmentManager, "addToFavouritesError")
            }
        }
    }

    fun deleteFromFavourites(index: Int, onSuccess: () -> Unit, onError: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
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
                val dialog = AlertFragment(e.message(), onError)
                dialog.show(parentFragmentManager, "deleteFavouriteError")
            }
        }
    }

    private fun getFavourites() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                favouritesList = retrofitService.getFavourites(apiKey)
                liveFavouritesList.value = favouritesList
                getNextPage() //getFavourites срабатывает только один раз
            } catch (e: HttpException) {
                val dialog = AlertFragment(e.message(), ::endLoading)
                dialog.show(parentFragmentManager, "getFavouritesError")
            }
        }
    }


}