package com.example.petside.di

import com.example.petside.data.db.Dao
import com.example.petside.data.repository.UserRepository
import dagger.Module
import dagger.Provides

@Module
class DataModule {
    @Provides
    fun provideUserRepository(dao: Dao): UserRepository {
        return UserRepository(dao)
    }
}