package com.example.petside.di

import com.example.domain.usecase.GetUserUseCase
import com.example.domain.usecase.SaveUserUseCase
import com.example.petside.data.repository.UserRepositoryImpl
import dagger.Module
import dagger.Provides

@Module
class DomainModule {
    @Provides
    fun provideGetUserUseCase(userRepositoryImpl: UserRepositoryImpl): GetUserUseCase {
        return GetUserUseCase(userRepositoryImpl)
    }

    @Provides
    fun provideSaveUserUseCase(userRepositoryImpl: UserRepositoryImpl): SaveUserUseCase {
        return SaveUserUseCase(userRepositoryImpl)
    }
}