package com.example.petside.di

import android.content.Context
import com.example.petside.view.ApiKeyFragment
import com.example.petside.view.AuthFragment
import com.example.petside.view.FeedFragment
import com.example.petside.viewmodel.ApiKeyViewModel
import com.example.petside.viewmodel.AuthViewModel
import com.example.petside.viewmodel.ImageFeedViewModel
import dagger.BindsInstance
import dagger.Component

@Component(modules = [AppModule::class, DatabaseModule::class, NetworkModule::class, DataModule::class, DomainModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent

    }

    fun inject(authViewModel: AuthViewModel)
    fun inject(apiKeyViewModel: ApiKeyViewModel)
    fun inject(imageFeedViewModel: ImageFeedViewModel)
}