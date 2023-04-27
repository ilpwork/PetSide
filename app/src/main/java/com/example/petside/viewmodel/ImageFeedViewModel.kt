package com.example.petside.viewmodel

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.petside.db.UserEntity
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

class ImageFeedViewModel @AssistedInject constructor(@Assisted val lifecycleOwner: LifecycleOwner, @Assisted  val parentFragmentManager: FragmentManager, val user: LiveData<UserEntity>, val retrofitService: RetrofitService): ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(lifecycleOwner: LifecycleOwner, parentFragmentManager: FragmentManager): ImageFeedViewModel
    }

    private lateinit var apiKey: String
    private var limit = 10
    private var page = 0

    var catImages = MutableLiveData<List<CatImage>>()
    var loading = false
    var hasMore = true

    init {
        user.observe(lifecycleOwner) {
            if (it !== null) {
                apiKey = it.api_key
                getNextPage()
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
                    catImages.value = newList
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