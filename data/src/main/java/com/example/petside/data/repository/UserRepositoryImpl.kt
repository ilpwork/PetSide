package com.example.petside.data.repository

import androidx.lifecycle.LiveData
import com.example.domain.model.User
import com.example.domain.repository.UserRepository
import com.example.petside.data.db.Dao
import com.example.petside.data.db.UserEntity

class UserRepositoryImpl(val dao: Dao): UserRepository {
    override fun saveUser(user: User) {
        dao.insertUser(user)
    }
    override fun getUser(): LiveData<User> {
        return dao.getUser()
    }
}