package com.example.petside.di

import com.example.petside.data.db.Dao
import com.example.petside.data.repository.CatImageRepository
import com.example.petside.data.repository.UserRepository
import com.example.petside.data.retrofit.RetrofitService
import dagger.Module
import dagger.Provides

@Module
class DataModule {
    @Provides
    fun provideUserRepository(dao: Dao): UserRepository {
        return UserRepository(dao)
    }

    @Provides
    fun provideCatImageRepository(retrofitService: RetrofitService): CatImageRepository {
        return CatImageRepository(retrofitService, apiKey = "")
    }
}