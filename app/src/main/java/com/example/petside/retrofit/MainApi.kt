package com.example.petside.retrofit

import retrofit2.http.Body
import retrofit2.http.POST

interface MainApi {
    @POST("v1/user/passwordlesssignup")
    suspend fun auth(@Body authRequest: AuthRequest)
}