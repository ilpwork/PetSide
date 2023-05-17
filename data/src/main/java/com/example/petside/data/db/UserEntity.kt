package com.example.petside.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey
    var id: Int = 0,
    @ColumnInfo(name = "email")
    var email: String = "",
    @ColumnInfo(name = "description")
    var description: String = "",
    @ColumnInfo(name = "api_key")
    var api_key: String = ""
)

/*
data class ApiKeyUpdate(
    val id: Int = 0,
    val api_key: String
)*/
