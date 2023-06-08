package com.example.domain.usecase

import com.example.domain.model.User
import com.example.domain.repository.UserRepository

class SaveUserUseCase(val userRepository: UserRepository) {
    fun execute(user: User) {
        userRepository.saveUser(user)
    }
}