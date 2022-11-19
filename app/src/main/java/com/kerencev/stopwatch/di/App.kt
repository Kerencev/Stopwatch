package com.kerencev.stopwatch.di

import android.app.Application
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        startKoin {
            modules(appModule)
        }
        super.onCreate()
    }
}