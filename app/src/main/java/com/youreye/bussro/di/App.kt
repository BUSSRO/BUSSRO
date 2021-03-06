package com.youreye.bussro.di

import android.app.Application
import com.youreye.bussro.util.BussroExceptionHandler
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        BussroExceptionHandler.setCrashHandler(this)
        instance = this
    }
}