package com.example.petside.retrofit

data class AuthRequest(
    val email: String,
    val appDescription: String,
    val details: AuthDetails = AuthDetails(),
    val opted_into_mailing_list: Boolean = false
)

