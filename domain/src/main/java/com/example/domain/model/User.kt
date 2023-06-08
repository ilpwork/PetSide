package com.example.domain.model

data class User(
    var id: Int = 0,
    var email: String = "",
    var description: String = "",
    var api_key: String = ""
)