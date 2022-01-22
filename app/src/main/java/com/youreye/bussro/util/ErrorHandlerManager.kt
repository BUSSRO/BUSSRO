package com.youreye.bussro.util

import android.app.Application

object ErrorHandlerManager {
    fun setCrashHandler(application: Application) {
        val crashlyticsExceptionHandler = Thread.getDefaultUncaughtExceptionHandler() ?: return
        Thread.setDefaultUncaughtExceptionHandler(
            BussroExceptionHandler(
                application,
                crashlyticsExceptionHandler
            )
        )
    }
}