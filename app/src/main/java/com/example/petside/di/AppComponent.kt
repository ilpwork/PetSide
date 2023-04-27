package com.example.petside.di

import android.content.Context
import com.example.petside.view.ApiKeyFragment
import com.example.petside.view.AuthFragment
import com.example.petside.view.FeedFragment
import dagger.BindsInstance
import dagger.Component

@Component(modules = [AppModule::class, DatabaseModule::class, NetworkModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent

    }

    fun inject(apiKeyFragment: ApiKeyFragment)
    fun inject(authFragment: AuthFragment)
    fun inject(feedFragment: FeedFragment)
}