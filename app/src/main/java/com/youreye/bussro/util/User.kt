package com.youreye.bussro.util

import android.content.Context

/**
 * [User]
 * 사용자 데이터 저장 SharedPreference
 */

object User {
    private const val FIRST_KEY = "isFirst"

    /* 앱 접속이력 */
    fun isFirst(context: Context) :Boolean {
        val sharedPreferences = context.getSharedPreferences(FIRST_KEY, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(FIRST_KEY, true)
    }

    fun setFirst(context: Context, flag: Boolean) {
        val sharedPreferences = context.getSharedPreferences(FIRST_KEY, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(FIRST_KEY, flag).apply()
    }
}