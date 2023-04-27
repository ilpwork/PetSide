package com.example.petside.viewmodel

import androidx.lifecycle.*
import com.example.petside.model.CatImage
import com.example.petside.retrofit.RetrofitService
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ImageFeedViewModel @AssistedInject constructor(@Assisted val apiKey: String): ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(apiKey: String): ImageFeedViewModel
    }

    var catImages = MutableLiveData<List<CatImage>>()
    private var list = ArrayList<CatImage>()

    private var limit = 10
    private var page = 0
    var loading = false
    var hasMore = true

    fun getNextPage() {
        if (hasMore) {
            loading = true
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.thecatapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val retrofitService = retrofit.create(RetrofitService::class.java)

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val newList = retrofitService.getCatImages(
                        apiKey,
                        limit,
                        page
                    )
                    list.addAll(newList)
                    catImages.value = list
                    page += 1
                    hasMore = newList.size == 10
                    loading = false
                } catch (e: HttpException) {
                    /*val dialog = AlertFragment(e.message(), ::endLoading)
                dialog.show(parentFragmentManager, "ApiKeyError")*/
                }
            }
        }
    }

}