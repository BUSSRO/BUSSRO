package com.example.bussro.util

import android.content.Context
import androidx.preference.PreferenceManager

/**
 * [SettingManager]
 * SettingsActivity 에서 사용자가 설정한 환경 설정값 return method
 */

object SettingManager {
    /* 음성안내 ON/OFF */
    fun getTTSSetting(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getBoolean("tts", false)
    }
}