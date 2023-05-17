package com.example.petside.data.repository

import androidx.lifecycle.LiveData
import com.example.petside.data.db.Dao
import com.example.petside.data.db.UserEntity

class UserRepository(val dao: Dao) {
    fun saveUser(user: UserEntity) {
        dao.insertUser(user)
    }
    fun getUser(): LiveData<UserEntity> {
        return dao.getUser()
    }
}