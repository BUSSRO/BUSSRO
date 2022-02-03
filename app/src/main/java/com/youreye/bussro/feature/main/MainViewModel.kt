package com.youreye.bussro.feature.main

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import com.youreye.bussro.feature.history.HistoryActivity
import com.youreye.bussro.feature.busstop.BusStopActivity
import com.youreye.bussro.feature.settings.SettingsActivity
import org.tensorflow.lite.examples.detection.DetectorActivity
import java.lang.Exception

/**
 * [MainViewModel]
 * MainActivity 의 ViewModel
 */

class MainViewModel : ViewModel() {

    /* onClick */
    fun onClick(v: View, case: Int) {
        when (case) {
            0 -> {  // 사용자 환경 설정
                v.context.startActivity(Intent(v.context, SettingsActivity::class.java))
            }
            1 -> {  // 버스 탑승 도우미
                v.context.startActivity(Intent(v.context, BusStopActivity::class.java))
            }
            2 -> {  // 하차벨 위치
                val intent = Intent(v.context, DetectorActivity::class.java)
                    .putExtra("from", "MainActivity")
                v.context.startActivity(intent)
            }
            3 -> {  // 히스토리
                v.context.startActivity(Intent(v.context, HistoryActivity::class.java))
            }
        }
    }
}