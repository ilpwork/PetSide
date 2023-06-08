package com.example.domain.repository

import androidx.lifecycle.LiveData
import com.example.domain.model.User

interface UserRepository {
    fun saveUser(user: User)
    fun getUser(): LiveData<User>
}