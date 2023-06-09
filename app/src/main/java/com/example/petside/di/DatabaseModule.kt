package com.example.petside.di

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.petside.data.db.AppDatabase
import com.example.petside.data.db.Dao
import com.example.petside.data.db.UserEntity
import dagger.Module
import dagger.Provides

@Module
class DatabaseModule {
    @Provides
    fun provideAppDatabase(context: Context): AppDatabase {
        return AppDatabase.getAppDatabase(context = context)
    }
    @Provides
    fun provideDao(appDatabase: AppDatabase): Dao {
        return appDatabase.getDao()
    }
}