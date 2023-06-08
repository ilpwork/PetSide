package com.example.domain.usecase

import androidx.lifecycle.LiveData
import com.example.domain.model.User
import com.example.domain.repository.UserRepository

class GetUserUseCase(val userRepository: UserRepository) {
    fun execute(): LiveData<User> {
        return userRepository.getUser()
    }
}