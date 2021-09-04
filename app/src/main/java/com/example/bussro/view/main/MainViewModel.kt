package com.example.bussro.view.main

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.bussro.view.busstop.NearbyBusStopActivity
import com.example.bussro.view.settings.SettingsActivity

/**
 * [MainViewModel]
 * MainActivity 의 ViewModel
 */

class MainViewModel : ViewModel() {

    /* onClick */
    fun onClick(v: View, case: Int) {
        var intent: Intent? = null
        var toast: Toast? = null

        when (case) {
            0 -> {  // 사용자 환경 설정
                intent = Intent(v.context, SettingsActivity::class.java)
            }
            1 -> {  // 버스 탑승 도우미
                intent = Intent(v.context, NearbyBusStopActivity::class.java)
            }
            2 -> {  // 하차벨 위치
                // TODO: implement StopOverActivity
                toast = Toast.makeText(v.context, "[하차벨 위치]\n구현되지 않은 기능입니다.", Toast.LENGTH_SHORT)
            }
            3 -> {  // 히스토리
                // TODO: implement HistoryActivity
                toast = Toast.makeText(v.context, "[히스토리]\n구현되지 않은 기능입니다.", Toast.LENGTH_SHORT)
            }
        }

        intent?.apply {
            v.context.startActivity(this)
        }

        toast?.apply {
            show()
        }
    }
}