package com.example.petside.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.example.petside.data.retrofit.RetrofitService

class CatImageRepository(val retrofitService: RetrofitService, val apiKey: String) {
    fun getCatImages() = Pager(config = PagingConfig(pageSize = 10),
        pagingSourceFactory = { CatImagesPagingSource(retrofitService, apiKey) }).liveData
}