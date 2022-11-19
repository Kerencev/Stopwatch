package com.kerencev.stopwatch.di

import android.app.Application
import com.kerencev.stopwatch.di.appModule
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : Application() {

    override fun onCreate() {
        startKoin {
            modules(appModule)
        }
        super.onCreate()
    }
}