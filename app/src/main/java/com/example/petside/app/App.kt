package com.example.petside.app

import android.app.Application
import com.example.petside.di.AppComponent
import com.example.petside.di.DaggerAppComponent

class App : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
            .builder()
            .context(context = this)
            .build()

    }

}