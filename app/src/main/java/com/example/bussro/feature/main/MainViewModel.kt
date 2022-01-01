package com.example.bussro.feature.main

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.bussro.feature.history.HistoryActivity
import com.example.bussro.feature.nearbybusstop.NearbyBusStopActivity
import com.example.bussro.feature.settings.SettingsActivity

/**
 * [MainViewModel]
 * MainActivity 의 ViewModel
 *
 * TODO: "하차벨 위치" 버튼 클릭시 하차벨 액티비티로 넘어가기
 */

class MainViewModel : ViewModel() {

    /* onClick */
    fun onClick(v: View, case: Int) {
        when (case) {
            0 -> {  // 사용자 환경 설정
                v.context.startActivity(Intent(v.context, SettingsActivity::class.java))
            }
            1 -> {  // 버스 탑승 도우미
                v.context.startActivity(Intent(v.context, NearbyBusStopActivity::class.java))
            }
            2 -> {  // 하차벨 위치
                Toast.makeText(v.context, "[하차벨 위치]\n다음 업데이트에 포함될 기능입니다.", Toast.LENGTH_SHORT).show()
            }
            3 -> {  // 히스토리
                v.context.startActivity(Intent(v.context, HistoryActivity::class.java))
            }
        }
    }
}