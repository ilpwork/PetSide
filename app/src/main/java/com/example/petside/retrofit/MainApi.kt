package com.example.petside.retrofit

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface MainApi {
    @POST("v1/user/passwordlesssignup")
    suspend fun auth(@Body authRequest: AuthRequest)

    @GET("v1/images/search")
    suspend fun getCatImages(
        @Header("x-api-key") key: String,
        @Query("limit") limit: Int,
        @Query("page") page: Int,
        @Query("order") order: String = "DESC"
    ): List<CatImage>

    @GET("v1/favourites")
    suspend fun getFavourites(@Header("x-api-key") key: String)
}