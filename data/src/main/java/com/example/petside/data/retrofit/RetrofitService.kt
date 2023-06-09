package com.example.petside.data.retrofit

import com.example.domain.model.CatImage
import com.example.domain.model.FavouriteImage
import retrofit2.http.*

interface RetrofitService {
    @POST("v1/user/passwordlesssignup")
    suspend fun auth(@Body authRequest: AuthRequest)

    @GET("v1/images/search")
    suspend fun getCatImages(
        @Header("x-api-key") key: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int = 10,
        @Query("order") order: String = "DESC"
    ): List<CatImage>

    @POST("v1/favourites")
    suspend fun addToFavourites(
        @Header("x-api-key") key: String,
        @Body favouritesRequest: FavouritesRequest
    ): FavouriteImage

    @GET("v1/favourites")
    suspend fun getFavourites(@Header("x-api-key") key: String): ArrayList<FavouriteImage>

    @DELETE("v1/favourites/{favouriteId}")
    suspend fun deleteFromFavourites(
        @Header("x-api-key") key: String,
        @Path("favouriteId") favouriteId: String
    )
}