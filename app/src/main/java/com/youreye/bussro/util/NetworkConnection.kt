package com.youreye.bussro.util

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.*
import android.os.Build
import androidx.lifecycle.LiveData
import com.youreye.bussro.di.App
import java.lang.IllegalArgumentException

/**
 * [NetworkConnection]
 * 사용자 디바이스의 네트워크 연결 확인
 */

object NetworkConnection {
    private var connectivityManager = App.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /* 인터넷 연결 확인 */
    fun isConnected() = connectivityManager.activeNetwork != null
}