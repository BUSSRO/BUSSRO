package com.example.bussro.util

import android.util.Log
import com.example.bussro.BuildConfig

fun Any.logd(message: String) {
    if (BuildConfig.DEBUG) {
        val simpleName = this::class.java.simpleName
        val tag = if (simpleName.length <= 19) simpleName else simpleName.substring(0, 19)

        Log.d("Log_$tag", message)
    }
}