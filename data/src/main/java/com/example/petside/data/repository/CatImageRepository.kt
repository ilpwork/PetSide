package com.example.petside.data.repository

import com.example.petside.data.retrofit.RetrofitService

class CatImageRepository(val retrofitService: RetrofitService, val apiKey: String) {
    fun getPagingSource() = CatImagesPagingSource(retrofitService, apiKey)
}