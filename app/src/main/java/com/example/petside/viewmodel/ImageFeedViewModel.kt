package com.example.petside.viewmodel

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.*
import com.example.petside.model.CatImage
import com.example.petside.retrofit.RetrofitService
import com.example.petside.view.AlertFragment
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

class ImageFeedViewModel @AssistedInject constructor(@Assisted val apiKey: String, @Assisted val parentFragmentManager: FragmentManager): ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(apiKey: String, parentFragmentManager: FragmentManager): ImageFeedViewModel
    }

    @Inject
    lateinit var retrofitService: RetrofitService

    private var list = ArrayList<CatImage>()
    private var limit = 10
    private var page = 0

    var catImages = MutableLiveData<List<CatImage>>()
    var loading = false
    var hasMore = true

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
                    list.addAll(newList)
                    catImages.value = list
                    page++
                    hasMore = newList.size == 10
                    loading = false
                } catch (e: HttpException) {
                    val dialog = AlertFragment(e.message(), ::endLoading)
                    dialog.show(parentFragmentManager, "ApiKeyError")
                }
            }
        }
    }

    fun endLoading() {

    }

}